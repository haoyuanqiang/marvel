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
import org.marvel.admin.entity.PermissionEntity;
import org.marvel.admin.model.AcceptPermission;
import org.marvel.admin.service.MenuService;
import org.marvel.admin.service.PermissionService;
import org.marvel.common.annotation.ResponseFormat;
import org.marvel.common.exception.ExceptionFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class PermissionController {
    @Resource
    private PermissionService permissionService;

    @Resource
    private MenuService menuService;

    /**
     * 获取权限列表
     *
     * @return 全部权限
     */
    @GetMapping("/permissions")
    @ResponseFormat
    public List<AcceptPermission> getPermissionList(@RequestParam(required = false) String menuId) {
        List<PermissionEntity> entities = permissionService.getListByMenuId(menuId);
        return CollectionUtil.map(entities, item -> BeanUtil.copyProperties(
                item,
                AcceptPermission.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid
        ), true);
    }

    /**
     * 获取权限
     *
     * @param permissionId 权限ID
     * @return 权限ID
     */
    @GetMapping("/permission")
    @ResponseFormat
    public AcceptPermission getPermission(@RequestParam("id") String permissionId) {
        PermissionEntity permission = permissionService.get(permissionId);
        if (!permissionService.isExistent(permission.getId())) {
            // nonexistent entity
            throw ExceptionFactory.create(1166);
        }
        return BeanUtil.copyProperties(
                permission,
                AcceptPermission.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid
        );
    }

    /**
     * 新增权限
     *
     * @param permission 权限
     * @return 权限ID
     */
    @PostMapping("/permission")
    @ResponseFormat
    public String addPermission(@RequestBody AcceptPermission permission) {
        int validateStatus = permission.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (!menuService.isExistent(permission.getMenuId())) {
            throw ExceptionFactory.create(1167);
        }

        PermissionEntity entity = new PermissionEntity();
        BeanUtil.copyProperties(permission, entity);
        entity.setId(null);
        permissionService.save(entity);
        return entity.getId();
    }

    /**
     * 修改权限
     *
     * @param permission 权限
     */
    @PutMapping("/permission")
    @ResponseFormat
    public void updatePermission(@RequestBody AcceptPermission permission) {
        int validateStatus = permission.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (!permissionService.isExistent(permission.getId())) {
            // nonexistent entity
            throw ExceptionFactory.create(1166);
        }
        if (!menuService.isExistent(permission.getMenuId())) {
            throw ExceptionFactory.create(1167);
        }
        PermissionEntity entity = permissionService.get(permission.getId());
        BeanUtil.copyProperties(permission, entity);
        permissionService.save(entity);
    }

    /**
     * 软删除权限
     *
     * @param permissionIds 权限ID列表
     */
    @PutMapping("/permissions/invalidation")
    @ResponseFormat
    public int invalidatePermissions(@RequestBody List<String> permissionIds) {
        if (ObjectUtil.isNotNull(permissionIds)) {
            return permissionService.invalidate(permissionIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个权限
     *
     * @param permissionId 权限ID
     */
    @DeleteMapping("/permission/{id}")
    @ResponseFormat
    public int deletePermission(@PathVariable("id") String permissionId) {
        if (StrUtil.isNotEmpty(permissionId)) {
            return permissionService.delete(permissionId);
        }
        return 0;
    }
}
