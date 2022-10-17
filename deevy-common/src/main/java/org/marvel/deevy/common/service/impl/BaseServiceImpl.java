package org.marvel.deevy.common.service.impl;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.enums.EntityValidValue;
import org.marvel.deevy.common.model.PageResult;
import org.marvel.deevy.common.repository.BaseRepository;
import org.marvel.deevy.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 基础服务实现
 *
 * @author haoyuanqiang
 * @date 2022/4/11 13:56
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
public class BaseServiceImpl<EntityType extends BaseEntity, IRepository extends BaseRepository<EntityType, String>>
        implements BaseService<EntityType> {

    // ===================================================== //
    //                     静态变量和方法区                     //
    // ===================================================== //

    protected static char ESCAPE_CHAR = '\\';

    /**
     * 特殊字符转义
     *
     * @param value 待转义的字符串
     * @return 转义后的字符串
     */
    protected static String escapeString(String value) {
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
     * @param value 待添加的字符串
     * @return 添加后的字符串
     */
    protected static String attachWildcard(String value) {
        return StrUtil.format("%{}%", escapeString(value));
    }


    // ===================================================== //
    //                     类属性和方法区                       //
    // ===================================================== //

    @Autowired
    protected IRepository repository;

    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * 获取实体类型名称
     *
     * @return 实体类型名称
     */
    @SuppressWarnings("unchecked")
    protected String getEntityTypeName() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        Class<EntityType> entityTypeClass =
                (Class<EntityType>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        return entityTypeClass.getName();
    }

    /**
     * 获取实体仓库名称
     *
     * @return 实体仓库名称
     */
    @SuppressWarnings("unchecked")
    protected String getRepositoryName() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        Class<IRepository> repositoryClass =
                (Class<IRepository>) ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        return repositoryClass.getName();
    }


    /**
     * 判断实体的某些属性是否重复
     *
     * @param matches   匹配的属性
     * @param ignorance 忽略的属性
     * @return 判断结果
     */
    public boolean isDuplicate(Map<String, Object> matches, Map<String, Object> ignorance) {
        Specification<EntityType> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> conditions = new ArrayList<>();
            if (ObjectUtil.isNotNull(matches)) {
                for (String attributeName : matches.keySet()) {
                    conditions.add((criteriaBuilder.equal(root.get(attributeName), matches.get(attributeName))));
                }
                Predicate matchCondition = criteriaBuilder.and(conditions.toArray(new Predicate[0]));
                conditions.clear();
                conditions.add(matchCondition);
            }
            if (ObjectUtil.isNotNull(ignorance)) {
                for (String attributeName : ignorance.keySet()) {
                    conditions.add(criteriaBuilder.notEqual(root.get(attributeName), ignorance.get(attributeName)));
                }
            }
            if (conditions.size() > 0) {
                criteriaQuery.where(criteriaBuilder.and(conditions.toArray(new Predicate[0])));
            }
            return null;
        };
        return repository.count(specification) > 0;
    }

    /**
     * 设置实体基础属性
     *
     * @param baseEntity 实体
     */
    protected void setBaseEntityProperties(BaseEntity baseEntity) {
        long now = System.currentTimeMillis();
        if (StrUtil.isBlank(baseEntity.getId())) {
            baseEntity.setId(UUID.randomUUID().toString());
        }
        if (ObjectUtil.isNull(baseEntity.getCreateTime())) {
            baseEntity.setCreateTime(now);
        }
        baseEntity.setModifyTime(now);
        if (null == baseEntity.getValid() || EntityValidValue.Invalid.ordinal() == baseEntity.getValid()) {
            baseEntity.setValid(EntityValidValue.Valid.ordinal());
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
            setBaseEntityProperties(entity);
            return repository.saveAndFlush(entity);
        }
        return null;
    }

    /**
     * 批量保存实体
     *
     * @param entities 实体列表
     * @return 保存后的实体列表
     */
    @Override
    public List<EntityType> saveAll(List<EntityType> entities) {
        if (ObjectUtil.isNotNull(entities)) {
            for (EntityType entity : entities) {
                if (ObjectUtil.isNotNull(entity)) {
                    setBaseEntityProperties(entity);
                }
            }
            repository.saveAll(entities);
            repository.flush();
        }
        return entities;
    }

    /**
     * 删除实体（软删除）
     *
     * @param id 实体 id
     * @return 删除成功的实体数量
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public int invalidate(String id) {
        if (StrUtil.isNotBlank(id)) {
            return 0;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Class<EntityType> entityClass = (Class<EntityType>) ClassLoaderUtil.loadClass(getEntityTypeName());
        CriteriaUpdate<EntityType> update = criteriaBuilder.createCriteriaUpdate(entityClass);
        Root<EntityType> root = update.from(entityClass);
        update.set(root.get(BaseEntity.Fields.valid), EntityValidValue.Invalid.ordinal());
        update.where(
                criteriaBuilder.equal(root.get(BaseEntity.Fields.id), id),
                criteriaBuilder.notEqual(root.get(BaseEntity.Fields.valid), EntityValidValue.Invalid.ordinal())
        );
        Query query = entityManager.createQuery(update);
        return query.executeUpdate();
    }

    /**
     * 批量删除实体（软删除）
     *
     * @param ids 实体的 id 列表 ids
     * @return 删除成功的实体数量
     */
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
        update.where(in, builder.notEqual(root.get(BaseEntity.Fields.valid), EntityValidValue.Invalid.ordinal())
        );
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
        update.set(root.get(BaseEntity.Fields.valid), EntityValidValue.Invalid.ordinal());
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
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = {RuntimeException.class})
    public int delete(List<String> ids) {
        if (ObjectUtil.isNull(ids) || ids.size() == 0) {
            return 0;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Class<EntityType> entityClass = (Class<EntityType>) ClassLoaderUtil.loadClass(getEntityTypeName());
        CriteriaDelete<EntityType> delete = builder.createCriteriaDelete(entityClass);
        Root<EntityType> root = delete.from(entityClass);
        CriteriaBuilder.In<String> in = builder.in(root.get(BaseEntity.Fields.id));
        for (String id : ids) {
            in.value(id);
        }
        delete.where(in);
        Query query = entityManager.createQuery(delete);
        return query.executeUpdate();
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

    /**
     * 判断实体是否存在
     *
     * @param id 实体 id
     * @return 判断结果
     */
    @Override
    public Boolean isExistent(String id) {
        if (StrUtil.isBlank(id)) {
            return false;
        }
        Specification<EntityType> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.where(
                    criteriaBuilder.equal(root.get(BaseEntity.Fields.id), id),
                    criteriaBuilder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
            );
            return null;
        };
        return repository.count(specification) > 0;
    }

    /**
     * 根据 id 获取实体信息
     *
     * @param id 实体 id
     * @return 实体信息
     */
    @Override
    public EntityType getById(String id) {
        if (StrUtil.isBlank(id)) {
            return null;
        }
        Specification<EntityType> specification = (root, query, builder) -> {
            query.where(
                    builder.equal(root.get(BaseEntity.Fields.id), id),
                    builder.notEqual(root.get(BaseEntity.Fields.valid), EntityValidValue.Invalid.ordinal())
            );
            return null;
        };
        return repository.findOne(specification).orElse(null);
    }

    /**
     * 获取全部实体信息
     *
     * @return 实体列表
     */
    @Override
    public List<EntityType> getAll() {
        Specification<EntityType> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(BaseEntity.Fields.modifyTime)));
            return builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal());
        };
        return repository.findAll(specification);
    }

    /**
     * 分页查询
     *
     * @param pageSize      页大小
     * @param current       当前页
     * @param specification 条件查询表达式
     * @return 分页查询结果
     */
    @Override
    public PageResult<EntityType> getByPage(int pageSize, int current, Specification<EntityType> specification) {
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        Page<EntityType> page = repository.findAll(specification, pageable);
        return new PageResult<>(page.getContent(), pageSize, current, page.getTotalElements());
    }


    @Override
    public List<EntityType> getAll(Specification<EntityType> specification) {
        return repository.findAll(specification);
    }
}
