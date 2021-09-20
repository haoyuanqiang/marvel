/*
 * Licensed to the organization of MARVEL under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The MARVEL licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.marvel.admin.service.impl;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.marvel.admin.common.MarvelSpringBootContext;
import org.marvel.admin.entity.BaseEntity;
import org.marvel.admin.repository.BaseRepository;
import org.marvel.admin.service.BaseService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class BaseServiceImpl<EntityType extends BaseEntity, IRepository extends BaseRepository<EntityType, String>>
        implements BaseService<EntityType> {
    protected static char ESCAPE_CHAR = '\\';

    protected IRepository repository;

    @PersistenceContext
    protected EntityManager entityManager;

    protected JPAQueryFactory jpaQueryFactory;

    /**
     * 获取实体类型名称
     *
     * @return 实体类型名称
     */
    @SuppressWarnings("unchecked")
    private String getEntityTypeName() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        Class<EntityType> entityTypeClass =
                (Class<EntityType>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        return entityTypeClass.getName();
    }

    /**
     * 获取实体仓库类名称
     *
     * @return 实体仓库类名称
     */
    @SuppressWarnings("unchecked")
    private String getRepositoryName() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        Class<IRepository> repositoryClass =
                (Class<IRepository>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[1];
        return repositoryClass.getName();
    }

    /**
     * 处理特殊字符
     *
     * @param value 待处理的字符串
     * @return 处理后的字符串
     */
    protected String escapeString(String value) {
        if (StrUtil.isNullOrUndefined(value)) {
            return StrUtil.EMPTY;
        }
        return value.replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("%", "\\%");
    }

    /**
     * 添加通配符 %{}%
     *
     * @param value 待处理的字符串
     * @return 处理后的字符串
     */
    protected String attachWildcard(String value) {
        return StrUtil.format("%{}%", value);
    }

    /**
     * 判断实体的某些属性是否重复
     *
     * @param matches   匹配的属性
     * @param ignorance 忽略的属性
     * @return 判断结果
     */
    protected Boolean isDuplicate(Map<String, Object> matches, Map<String, Object> ignorance) {
        Specification<EntityType> spec = (root, query, builder) -> {
            List<Predicate> conditions = new ArrayList<>();
            if (ObjectUtil.isNotNull(matches)) {
                for (String attributeName : matches.keySet()) {
                    conditions.add(builder.equal(root.get(attributeName), matches.get(attributeName)));
                }
                Predicate matchCondition = builder.and(conditions.toArray(new Predicate[0]));
                conditions.clear();
                conditions.add(matchCondition);
            }
            if (ObjectUtil.isNotNull(ignorance)) {
                for (String attributeName : ignorance.keySet()) {
                    conditions.add(builder.notEqual(root.get(attributeName), ignorance.get(attributeName)));
                }
            }
            if (conditions.size() > 0) {
                query.where(builder.and(conditions.toArray(new Predicate[0])));
            }
            return null;
        };
        return repository.count(spec) > 0;
    }

    /**
     * 初始化
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    public void initialize() {
        if (ObjectUtil.isNull(repository)) {
            repository = (IRepository) MarvelSpringBootContext.getBean(ClassLoaderUtil.loadClass(getRepositoryName()));
        }
        if (ObjectUtil.isNull(jpaQueryFactory)) {
            jpaQueryFactory = new JPAQueryFactory(entityManager);
        }
    }

    /**
     * 处理实体的基础属性
     *
     * @param entity 实体
     */
    protected void handleBaseEntityProperty(BaseEntity entity) {
        long now = System.currentTimeMillis();
        if (ObjectUtil.isNull(entity.getId())) {
            entity.setId(IdUtil.objectId());
        }
        if (ObjectUtil.isNull(entity.getCreateTime())) {
            entity.setCreateTime(now);
        }
        entity.setModifyTime(now);
        if (ObjectUtil.isNull(entity.getValid()) || entity.getValid() == 0) {
            entity.setValid(1);
        }
    }

    /**
     * 保存实体
     *
     * @param entity 实体信息
     * @return 保存后的实体
     */
    @Override
    public EntityType save(EntityType entity) {
        if (ObjectUtil.isNotNull(entity)) {
            handleBaseEntityProperty(entity);
            return repository.saveAndFlush(entity);
        }
        return null;
    }

    /**
     * 批量保存实体
     *
     * @param entities 实体列表
     * @return 保存后的实体
     */
    @Override
    public List<EntityType> saveAll(List<EntityType> entities) {
        if (ObjectUtil.isNotNull(entities)) {
            for (EntityType entity : entities) {
                if (ObjectUtil.isNotNull(entity)) {
                    handleBaseEntityProperty(entity);
                }
            }
            repository.saveAll(entities);
            repository.flush();
        }
        return entities;
    }

    /**
     * 删除实体
     *
     * @param id 实体 ID
     * @return 删除数量
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public int invalidate(String id) {
        if (StrUtil.isEmpty(id)) {
            return 0;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Class<EntityType> entityClass = (Class<EntityType>) ClassLoaderUtil.loadClass(getEntityTypeName());
        CriteriaUpdate<EntityType> update = builder.createCriteriaUpdate(entityClass);
        Root<EntityType> root = update.from(entityClass);
        update.set(root.get(BaseEntity.Fields.valid), 0);
        update.where(
                builder.equal(root.get(BaseEntity.Fields.id), id),
                builder.notEqual(root.get(BaseEntity.Fields.valid), 0)
        );
        Query query = entityManager.createQuery(update);
        return query.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = {RuntimeException.class})
    public int invalidate(List<String> ids) {
        if (ObjectUtil.isNull(ids) || ids.size() == 0) {
            return 0;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Class<EntityType> entityClass = (Class<EntityType>) ClassLoaderUtil.loadClass(getEntityTypeName());
        CriteriaUpdate<EntityType> update = builder.createCriteriaUpdate(entityClass);
        Root<EntityType> root = update.from(entityClass);
        update.set(root.get(BaseEntity.Fields.valid), 0);
        CriteriaBuilder.In<String> in = builder.in(root.get(BaseEntity.Fields.id));
        for (String id : ids) {
            in.value(id);
        }
        update.where(in, builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
        Query query = entityManager.createQuery(update);
        return query.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = {RuntimeException.class})
    public int invalidate(Specification<EntityType> specification) {
        if (ObjectUtil.isNull(specification)) {
            return 0;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Class<EntityType> entityClass = (Class<EntityType>) ClassLoaderUtil.loadClass(getEntityTypeName());
        CriteriaUpdate<EntityType> update = builder.createCriteriaUpdate(entityClass);
        Root<EntityType> root = update.from(entityClass);
        update.set(root.get(BaseEntity.Fields.valid), 0);
        update.where(specification.toPredicate(root, null, builder));
        Query query = entityManager.createQuery(update);
        return query.executeUpdate();
    }

    @Override
    public int delete(String id) {
        if (StrUtil.isEmpty(id)) {
            return 0;
        }
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    @Override
    public int delete(List<String> ids) {
        if (ObjectUtil.isNull(ids) || ids.size() == 0) {
            return 0;
        }
        int deletedCount = 0;
        for (String id : ids) {
            deletedCount += delete(id);
        }
        return deletedCount;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = {RuntimeException.class})
    public int delete(Specification<EntityType> specification) {
        if (ObjectUtil.isNull(specification)) {
            return 0;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Class<EntityType> entityClass = (Class<EntityType>) ClassLoaderUtil.loadClass(getEntityTypeName());
        CriteriaDelete<EntityType> delete = builder.createCriteriaDelete(entityClass);
        Root<EntityType> root = delete.from(entityClass);
        delete.where(specification.toPredicate(root, null, builder));
        Query query = entityManager.createQuery(delete);
        return query.executeUpdate();
    }

    @Override
    public EntityType get(String id) {
        if (StrUtil.isEmpty(id)) {
            return null;
        }
        Specification<EntityType> spec = (root, query, builder) -> {
            query.where(
                    builder.equal(root.get(BaseEntity.Fields.id), id),
                    builder.notEqual(root.get(BaseEntity.Fields.valid), 0)
            );
            return null;
        };
        return repository.findOne(spec).orElse(null);
    }

    @Override
    public Boolean isExistent(String id) {
        if (StrUtil.isEmpty(id)) {
            return false;
        }
        Specification<EntityType> spec = (root, query, builder) -> {
            query.where(
                    builder.equal(root.get(BaseEntity.Fields.id), id),
                    builder.notEqual(root.get(BaseEntity.Fields.valid), 0)
            );
            return null;
        };
        return repository.count(spec) > 0;
    }
}
