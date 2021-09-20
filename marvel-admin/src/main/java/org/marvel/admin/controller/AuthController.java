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

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import org.marvel.admin.entity.PermissionEntity;
import org.marvel.admin.entity.RelationRolePermissionEntity;
import org.marvel.admin.entity.RoleEntity;
import org.marvel.admin.entity.UserLoginEntity;
import org.marvel.admin.service.PermissionService;
import org.marvel.admin.service.RoleService;
import org.marvel.admin.service.UserService;
import org.marvel.common.annotation.ResponseFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthController {
    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @Resource
    private PermissionService permissionService;

    /**
     * 校验用户密码
     *
     * @param username 用户名称
     * @param password 用户密码
     * @return 校验结果
     */
    @PostMapping("/auth/user/validation")
    @ResponseFormat
    public Boolean validateUser(@RequestParam String username, @RequestParam String password) {
        UserLoginEntity entity = userService.getUserLoginParam(username);
        if (ObjectUtil.isNotNull(entity)) {
            return entity.isMatchPassword(password);
        }
        return false;
    }

    /**
     * 根据用户获取角色列表
     *
     * @param username 用户名称
     * @return 角色列表
     */
    @GetMapping("/auth/user/roles")
    @ResponseFormat
    public List<RoleEntity> getUserRoleIds(@RequestParam String username) {
        UserLoginEntity entity = userService.getUserLoginParam(username);
        if (ObjectUtil.isNotNull(entity)) {
            return roleService.getRolesByUserId(entity.getId());
        }
        return new ArrayList<>(0);
    }

    /**
     * 获取角色权限
     *
     * @param roleIds 角色ID列表
     * @return 角色权限
     */
    @GetMapping("/auth/permissions")
    @ResponseFormat
    public List<PermissionEntity> getPermissions(@RequestParam List<String> roleIds) {
        List<PermissionEntity> result = new ArrayList<>();
        List<PermissionEntity> rolePermissions = roleService.getRolePermissions(roleIds,
                ListUtil.toList(RelationRolePermissionEntity.TYPE_PERMISSION));
        if (ObjectUtil.isNotNull(rolePermissions)) {
            result.addAll(rolePermissions);
        }
        List<PermissionEntity> unlimitedPermissions = permissionService.getUnlimitedPermissions();
        if (ObjectUtil.isNotNull(unlimitedPermissions)) {
            result.addAll(unlimitedPermissions);
        }
        return result;
    }


}
