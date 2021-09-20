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

package org.marvel.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.admin.entity.BaseEntity;
import org.marvel.admin.entity.RelationRolePermissionEntity;
import org.marvel.admin.entity.RoleEntity;
import org.marvel.admin.model.AcceptRole;
import org.marvel.admin.model.AcceptRolePermission;
import org.marvel.admin.service.RoleService;
import org.marvel.common.annotation.ResponseFormat;
import org.marvel.common.exception.ExceptionFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RoleController {
    @Resource
    private RoleService roleService;

    /**
     * 获取角色列表
     *
     * @return 全部角色
     */
    @GetMapping("/roles")
    @ResponseFormat
    public List<AcceptRole> getRoleList(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name
    ) {
        List<RoleEntity> entities = roleService.getList(
                ObjectUtil.defaultIfNull(pageSize, 20),
                ObjectUtil.defaultIfNull(current, 0),
                code,
                name
        );
        return CollectionUtil.map(entities, item -> BeanUtil.copyProperties(
                item,
                AcceptRole.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid
        ), true);
    }

    /**
     * 获取角色
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    @GetMapping("/role")
    @ResponseFormat
    public AcceptRole getRole(@RequestParam("id") String roleId) {
        RoleEntity role = roleService.get(roleId);
        return BeanUtil.copyProperties(
                role,
                AcceptRole.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid
        );
    }

    /**
     * 新增角色
     *
     * @param role 角色
     * @return 角色ID
     */
    @PostMapping("/role")
    @ResponseFormat
    public String addRole(@RequestBody AcceptRole role) {
        int validateStatus = role.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (roleService.isDuplicate(role.getCode(), null, null)) {
            // duplicate role code
            throw ExceptionFactory.create(1114);
        }
        if (roleService.isDuplicate(null, role.getName(), null)) {
            // duplicate role name
            throw ExceptionFactory.create(1115);
        }
        RoleEntity entity = new RoleEntity();
        BeanUtil.copyProperties(role, entity);
        entity.setId(null);
        roleService.save(entity);
        return entity.getId();
    }

    /**
     * 修改角色
     *
     * @param role 角色
     */
    @PutMapping("/role")
    @ResponseFormat
    public void updateRole(@RequestBody AcceptRole role) {
        int validateStatus = role.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (!roleService.isExistent(role.getId())) {
            // nonexistent entity
            throw ExceptionFactory.create(1116);
        }
        if (roleService.isDuplicate(role.getCode(), null, role.getId())) {
            // duplicate role code
            throw ExceptionFactory.create(1114);
        }
        if (roleService.isDuplicate(null, role.getName(), role.getId())) {
            // duplicate role name
            throw ExceptionFactory.create(1115);
        }
        RoleEntity entity = roleService.get(role.getId());
        BeanUtil.copyProperties(role, entity);
        roleService.save(entity);
    }

    /**
     * 软删除角色
     *
     * @param roleIds 角色ID列表
     */
    @PutMapping("/roles/invalidation")
    @ResponseFormat
    public int invalidateRoles(@RequestBody List<String> roleIds) {
        if (ObjectUtil.isNotNull(roleIds)) {
            for (String roleId : roleIds) {
                roleService.deleteRolePermissions(roleId, 0);
            }
            return roleService.invalidate(roleIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个角色
     *
     * @param roleId 角色ID
     */
    @DeleteMapping("/role/{id}")
    @ResponseFormat
    public int deleteRole(@PathVariable("id") String roleId) {
        if (StrUtil.isNotEmpty(roleId)) {
            roleService.deleteRolePermissions(roleId, 0);
            return roleService.delete(roleId);
        }
        return 0;
    }

    /**
     * 获取角色权限
     *
     * @param roleId 角色ID
     * @return 角色权限
     */
    @GetMapping("/role/permissions")
    @ResponseFormat
    public AcceptRolePermission getRolePermission(@RequestParam String roleId) {
        RoleEntity entity = roleService.get(roleId);
        if (ObjectUtil.isNull(entity)) {
            throw ExceptionFactory.create(1116);
        }
        AcceptRolePermission rolePermission = new AcceptRolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setRoleName(entity.getName());
        rolePermission.setMenuIds(roleService.getRoleMenuIds(roleId));
        rolePermission.setPermissionIds(roleService.getRolePermissionIds(roleId));
        return rolePermission;
    }

    /**
     * 保存角色权限
     *
     * @param rolePermission 角色权限
     */
    @PostMapping("/role/permissions")
    @ResponseFormat
    public void saveRolePermissions(@RequestBody(required = false) AcceptRolePermission rolePermission) {
        if (ObjectUtil.isNull(rolePermission)) {
            throw ExceptionFactory.create(1002);
        }
        if (!roleService.isExistent(rolePermission.getRoleId())) {
            throw ExceptionFactory.create(1116);
        }
        Map<Integer, List<String>> permissions = new LinkedHashMap<>();
        if (ObjectUtil.isNotNull(rolePermission.getMenuIds())) {
            permissions.put(RelationRolePermissionEntity.TYPE_MENU, rolePermission.getMenuIds());
        }
        if (ObjectUtil.isNotNull(rolePermission.getPermissionIds())) {
            permissions.put(RelationRolePermissionEntity.TYPE_PERMISSION, rolePermission.getPermissionIds());
        }
        if (ObjectUtil.isNotNull(rolePermission.getHalfCheckedMenuIds())) {
            permissions.put(RelationRolePermissionEntity.TYPE_HALF_CHECKED, rolePermission.getHalfCheckedMenuIds());
        }
        roleService.saveRolePermissions(rolePermission.getRoleId(), permissions);
    }
}
