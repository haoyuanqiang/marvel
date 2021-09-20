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

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.admin.entity.*;
import org.marvel.admin.model.Pagination;
import org.marvel.admin.repository.RelationPositionUserRepository;
import org.marvel.admin.repository.RelationRoleUserRepository;
import org.marvel.admin.repository.UserLoginRepository;
import org.marvel.admin.repository.UserRepository;
import org.marvel.admin.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl extends BaseServiceImpl<UserEntity, UserRepository> implements UserService {
    @Resource
    private UserLoginRepository userLoginRepository;

    @Resource
    private RelationRoleUserRepository roleUserRepository;

    @Resource
    private RelationPositionUserRepository positionUserRepository;

    @Override
    public Boolean isDuplicate(Map<String, Object> matches, Map<String, Object> ignorance) {
        return super.isDuplicate(matches, ignorance);
    }

    @Override
    public UserEntity saveUser(UserEntity entity) {
        boolean isCreate = StrUtil.isBlank(entity.getId());
        save(entity);
        if (ObjectUtil.isNotNull(entity)) {
            UserLoginEntity loginEntity = new UserLoginEntity();
            loginEntity.setId(entity.getId());
            loginEntity.setName(entity.getLoginName());
            loginEntity.setStatus(entity.getStatus());
            loginEntity.setCreateTime(entity.getCreateTime());
            loginEntity.setModifyTime(entity.getModifyTime());
            loginEntity.setValid(1);
            if (isCreate || StrUtil.isBlank(loginEntity.getPassword())) {
                loginEntity.setPassword(UserLoginEntity.encryptPassword(loginEntity.getName()));
            }
            userLoginRepository.save(loginEntity);
        }
        return entity;
    }

    @Override
    public Pagination<UserEntity> getList(int pageSize, int current, Map<String, Object> otherConditions) {
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        Specification<UserEntity> specification = ((root, query, builder) -> {
            List<Predicate> conditions = new ArrayList<>(3);
            conditions.add(builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
            if (ObjectUtil.isNotNull(otherConditions)) {
                for (String key : otherConditions.keySet()) {
                    if (StrUtil.contains("code,name,nickname,loginName", key)) {
                        Object value = otherConditions.get(key);
                        conditions.add(builder.like(root.get(key),
                                StrUtil.format("%{}%", escapeString(value.toString()))));
                    } else if (StrUtil.isNotBlank(key) && ReflectUtil.hasField(UserEntity.class, key)) {
                        conditions.add(builder.equal(root.get(key), otherConditions.get(key)));
                    }
                }
            }
            query.where(conditions.toArray(new Predicate[0]));
            query.orderBy(builder.asc(root.get(UserEntity.Fields.sortNumber)));
            return null;
        });
        Page<UserEntity> page = repository.findAll(specification, pageable);
        return new Pagination<>(page.getContent(), page.getTotalElements(), pageSize, current);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public int deleteUser(List<String> ids) {
        if (ObjectUtil.isNull(ids)) {
            return 0;
        }
        int count = invalidate(ids);
        for (String id : ids) {
            userLoginRepository.deleteById(id);
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public int deleteUserFully(List<String> ids) {
        if (ObjectUtil.isNull(ids)) {
            return 0;
        }
        int count = delete(ids);
        for (String id : ids) {
            userLoginRepository.deleteById(id);
        }
        return count;
    }

    @Override
    public UserLoginEntity getUserLoginParam(String loginName) {
        Specification<UserLoginEntity> specification = ((root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.where(criteriaBuilder.equal(root.get(UserLoginEntity.Fields.name), loginName));
            return null;
        });
        Optional<UserLoginEntity> optional = userLoginRepository.findOne(specification);
        return optional.orElse(null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public int updatePassword(String id, String newPassword) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<UserLoginEntity> update = builder.createCriteriaUpdate(UserLoginEntity.class);
        Root<UserLoginEntity> root = update.from(UserLoginEntity.class);
        update.set(root.get(UserLoginEntity.Fields.password), UserLoginEntity.encryptPassword(newPassword));
        update.where(
                builder.equal(root.get(BaseEntity.Fields.id), id),
                builder.notEqual(root.get(BaseEntity.Fields.valid), 0)
        );
        Query query = entityManager.createQuery(update);
        return query.executeUpdate();
    }

    // ----------------------------------------------------------------------- //
    // 用户、角色、权限关联关系操作
    // ----------------------------------------------------------------------- //

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void saveUserRoleIds(String userId, List<String> roleIds) {
        if (StrUtil.isBlank(userId) || ObjectUtil.isNull(roleIds)) {
            return;
        }
        deleteUserRoleIds(userId);
        List<RelationRoleUserEntity> entities = new ArrayList<>(roleIds.size());
        for (String roleId : roleIds) {
            RelationRoleUserEntity entity = new RelationRoleUserEntity();
            handleBaseEntityProperty(entity);
            entity.setUserId(userId);
            entity.setRoleId(roleId);
            entities.add(entity);
        }
        roleUserRepository.saveAll(entities);
    }

    @Override
    public List<String> getUserRoleIds(String userId) {
        Specification<RelationRoleUserEntity> specification = ((root, query, builder) -> {
            query.where(builder.equal(root.get(RelationRoleUserEntity.Fields.userId), userId));
            return null;
        });
        List<RelationRoleUserEntity> queryResult = roleUserRepository.findAll(specification);
        List<String> result = new ArrayList<>();
        if (ObjectUtil.isNotNull(queryResult)) {
            for (RelationRoleUserEntity roleUserEntity : queryResult) {
                result.add(roleUserEntity.getRoleId());
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public int deleteUserRoleIds(String userId) {
        if (StrUtil.isBlank(userId)) {
            return 0;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaDelete<RelationRoleUserEntity> delete = builder.createCriteriaDelete(RelationRoleUserEntity.class);
        Root<RelationRoleUserEntity> root = delete.from(RelationRoleUserEntity.class);
        delete.where(builder.equal(root.get(RelationRoleUserEntity.Fields.userId), userId));
        return entityManager.createQuery(delete).executeUpdate();
    }


    public String checkUserMenuAvailability(String userId, String menuPath) {
        QMenuEntity menuEntity = QMenuEntity.menuEntity;
        QRelationRoleUserEntity roleUserEntity = QRelationRoleUserEntity.relationRoleUserEntity;
        QRelationRolePermissionEntity rolePermissionEntity = QRelationRolePermissionEntity.relationRolePermissionEntity;
        List<String> ids = jpaQueryFactory.select(menuEntity.id)
                .from(roleUserEntity)
                .innerJoin(rolePermissionEntity).on(rolePermissionEntity.roleId.eq(roleUserEntity.roleId))
                .innerJoin(menuEntity).on(menuEntity.id.eq(rolePermissionEntity.permissionId))
                .where(roleUserEntity.userId.eq(userId), menuEntity.type.eq(MenuEntity.TYPE_PAGE), menuEntity.path.eq(menuPath))
                .fetch();
        return ids.size() > 0 ? ids.get(0) : null;
    }

    // ----------------------------------------------------------------------- //
    // 用户岗位关联关系操作
    // ----------------------------------------------------------------------- //

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void saveUserPositionIds(String userId, List<String> positionIds) {
        if (StrUtil.isBlank(userId) || ObjectUtil.isNull(positionIds)) {
            return;
        }
        deleteUserPositionIds(userId);
        List<RelationPositionUserEntity> entities = new ArrayList<>(positionIds.size());
        for (String positionId : positionIds) {
            RelationPositionUserEntity entity = new RelationPositionUserEntity();
            handleBaseEntityProperty(entity);
            entity.setUserId(userId);
            entity.setPositionId(positionId);
            entities.add(entity);
        }
        positionUserRepository.saveAll(entities);
    }

    @Override
    public List<String> getUserPositionIds(String userId) {
        Specification<RelationPositionUserEntity> specification = ((root, query, builder) -> {
            query.where(builder.equal(root.get(RelationPositionUserEntity.Fields.userId), userId));
            return null;
        });
        List<RelationPositionUserEntity> queryResult = positionUserRepository.findAll(specification);
        List<String> result = new ArrayList<>();
        if (ObjectUtil.isNotNull(queryResult)) {
            for (RelationPositionUserEntity positionUserEntity : queryResult) {
                result.add(positionUserEntity.getPositionId());
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public int deleteUserPositionIds(String userId) {
        if (StrUtil.isBlank(userId)) {
            return 0;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaDelete<RelationPositionUserEntity> delete = builder.createCriteriaDelete(RelationPositionUserEntity.class);
        Root<RelationPositionUserEntity> root = delete.from(RelationPositionUserEntity.class);
        delete.where(builder.equal(root.get(RelationPositionUserEntity.Fields.userId), userId));
        return entityManager.createQuery(delete).executeUpdate();
    }


}
