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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.admin.entity.*;
import org.marvel.admin.repository.RelationRolePermissionRepository;
import org.marvel.admin.repository.RoleRepository;
import org.marvel.admin.service.RoleService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl extends BaseServiceImpl<RoleEntity, RoleRepository> implements RoleService {

    @Resource
    private RelationRolePermissionRepository rolePermissionRepository;

    @Override
    public Boolean isDuplicate(String code, String name, String ignorantId) {
        Map<String, Object> matches = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(code)) {
            matches.put(RoleEntity.Fields.code, code);
        }
        if (StrUtil.isNotEmpty(name)) {
            matches.put(RoleEntity.Fields.name, name);
        }
        Map<String, Object> ignorance = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(ignorantId)) {
            ignorance.put(BaseEntity.Fields.id, ignorantId);
        }
        return super.isDuplicate(matches, ignorance);
    }

    @Override
    public List<RoleEntity> getList(int pageSize, int current, String code, String name) {
        Pageable pageable = PageRequest.of(current, pageSize);
        Specification<RoleEntity> spec = (root, query, builder) -> {
            List<Predicate> conditions = new ArrayList<>(3);
            conditions.add(builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
            if (StrUtil.isNotBlank(code)) {
                conditions.add(builder.like(root.get(RoleEntity.Fields.code),
                        StrUtil.format("%{}%", escapeString(code))));
            }
            if (StrUtil.isNotBlank(name)) {
                conditions.add(builder.like(root.get(RoleEntity.Fields.name),
                        StrUtil.format("%{}%", escapeString(name))));
            }
            query.where(conditions.toArray(new Predicate[0]));
            query.orderBy(builder.asc(root.get(RoleEntity.Fields.sortNumber)));
            return null;
        };
        return repository.findAll(spec);
    }

    // --------------------------------------------------------------------- //
    // 角色权限操作接口
    // --------------------------------------------------------------------- //

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void saveRolePermissions(String roleId, Map<Integer, List<String>> permissions) {
        if (StrUtil.isBlank(roleId)) {
            return;
        }
        List<RelationRolePermissionEntity> entities = new ArrayList<>();
        for (Integer type : permissions.keySet()) {
            deleteRolePermissions(roleId, type);
            List<String> permissionIds = permissions.get(type);
            if (ObjectUtil.isNotNull(permissionIds)) {
                for (String permissionId : permissionIds) {
                    RelationRolePermissionEntity entity = new RelationRolePermissionEntity();
                    handleBaseEntityProperty(entity);
                    entity.setRoleId(roleId);
                    entity.setPermissionId(permissionId);
                    entity.setType(type);
                    entities.add(entity);
                }
            }
        }
        rolePermissionRepository.saveAll(entities);
    }

    @Override
    public List<String> getRoleMenuIds(String roleId) {
        Specification<RelationRolePermissionEntity> specification = ((root, query, builder) -> {
            query.where(builder.equal(root.get(RelationRolePermissionEntity.Fields.roleId), roleId),
                    builder.equal(root.get(RelationRolePermissionEntity.Fields.type), RelationRolePermissionEntity.TYPE_MENU));
            return null;
        });
        List<RelationRolePermissionEntity> queryResult = rolePermissionRepository.findAll(specification);
        List<String> result = new ArrayList<>();
        if (ObjectUtil.isNotNull(queryResult)) {
            for (RelationRolePermissionEntity rolePermissionEntity : queryResult) {
                result.add(rolePermissionEntity.getPermissionId());
            }
        }
        return result;
    }


    @Override
    public List<String> getRolesMenuIds(List<String> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return new ArrayList<>(0);
        }
        Specification<RelationRolePermissionEntity> specification = ((root, query, builder) -> {
            CriteriaBuilder.In<String> in = builder.in(root.get(RelationRolePermissionEntity.Fields.roleId));
            for (String roleId : roleIds) {
                in.value(roleId);
            }
            query.where(in, builder.equal(root.get(RelationRolePermissionEntity.Fields.type),
                    RelationRolePermissionEntity.TYPE_MENU));
            return null;
        });
        List<RelationRolePermissionEntity> queryResult = rolePermissionRepository.findAll(specification);
        List<String> result = new ArrayList<>();
        if (ObjectUtil.isNotNull(queryResult)) {
            for (RelationRolePermissionEntity rolePermissionEntity : queryResult) {
                result.add(rolePermissionEntity.getPermissionId());
            }
        }
        return result;
    }

    @Override
    public List<String> getRolePermissionIds(String roleId) {
        Specification<RelationRolePermissionEntity> specification = ((root, query, builder) -> {
            query.where(builder.equal(root.get(RelationRolePermissionEntity.Fields.roleId), roleId),
                    builder.equal(root.get(RelationRolePermissionEntity.Fields.type), RelationRolePermissionEntity.TYPE_PERMISSION));
            return null;
        });
        List<RelationRolePermissionEntity> queryResult = rolePermissionRepository.findAll(specification);
        List<String> result = new ArrayList<>();
        if (ObjectUtil.isNotNull(queryResult)) {
            for (RelationRolePermissionEntity rolePermissionEntity : queryResult) {
                result.add(rolePermissionEntity.getPermissionId());
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public int deleteRolePermissions(String roleId, int type) {
        if (StrUtil.isBlank(roleId)) {
            return 0;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaDelete<RelationRolePermissionEntity> delete = builder.createCriteriaDelete(RelationRolePermissionEntity.class);
        Root<RelationRolePermissionEntity> root = delete.from(RelationRolePermissionEntity.class);
        List<Predicate> conditions = new ArrayList<>(2);
        conditions.add(builder.equal(root.get(RelationRolePermissionEntity.Fields.roleId), roleId));
        if (type == RelationRolePermissionEntity.TYPE_MENU || type == RelationRolePermissionEntity.TYPE_PERMISSION) {
            conditions.add(builder.equal(root.get(RelationRolePermissionEntity.Fields.type), type));
        }
        delete.where(conditions.toArray(new Predicate[0]));
        return entityManager.createQuery(delete).executeUpdate();
    }

    @Override
    public List<PermissionEntity> getRolePermissions(List<String> roleIds, List<Integer> types) {
        if (ObjectUtil.isNull(roleIds) || roleIds.size() == 0) {
            return new ArrayList<>(0);
        }
        QPermissionEntity permissionEntity = QPermissionEntity.permissionEntity;
        QRelationRolePermissionEntity relationEntity = QRelationRolePermissionEntity.relationRolePermissionEntity;
        List<com.querydsl.core.types.Predicate> conditions = new ArrayList<>();
        conditions.add(relationEntity.permissionId.eq(permissionEntity.id));
        conditions.add(relationEntity.roleId.in(roleIds));
        conditions.add(relationEntity.valid.ne(0));
        if (ObjectUtil.isNotNull(types)) {
            conditions.add(relationEntity.type.in(types));
        }
        conditions.add(permissionEntity.valid.ne(0));
        return jpaQueryFactory.select(permissionEntity)
                .from(relationEntity, permissionEntity)
                .where(conditions.toArray(new com.querydsl.core.types.Predicate[0]))
                .orderBy(permissionEntity.sortNumber.asc())
                .fetch();
    }

    // --------------------------------------------------------------------- //
    // 用户角色操作接口
    // --------------------------------------------------------------------- //

    @Override
    public List<RoleEntity> getRolesByUserId(String userId) {
        QRelationRoleUserEntity relationEntity = QRelationRoleUserEntity.relationRoleUserEntity;
        QRoleEntity roleEntity = QRoleEntity.roleEntity;
        return jpaQueryFactory.select(roleEntity)
                .from(relationEntity, roleEntity)
                .where(relationEntity.roleId.eq(roleEntity.id), relationEntity.userId.eq(userId))
                .orderBy(roleEntity.modifyTime.desc())
                .fetch();
    }
}
