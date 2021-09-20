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

package org.marvel.auth.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.marvel.auth.model.MarvelResponse;
import org.marvel.auth.model.Permission;
import org.marvel.auth.model.Role;
import org.marvel.auth.service.FeignBasisService;
import org.marvel.auth.service.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    private final Cache<String, List<String>> roleCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis());
    private final Cache<String, Set<String>> permissionCache = CacheUtil.newTimedCache(DateUnit.MINUTE.getMillis());
    @Resource
    private FeignBasisService feignBasisService;

    @Override
    public Boolean checkUser(String username, String password) {
        MarvelResponse<Boolean> response = feignBasisService.validateUser(username, password);
        if (ObjectUtil.isNull(response)) {
            return false;
        }
        return response.getResult();
    }

    @Override
    public List<String> getUserRoleIds(String username) {
        if (roleCache.containsKey(username)) {
            return roleCache.get(username);
        }
        MarvelResponse<List<Role>> response = feignBasisService.getUserRoleIds(username);
        if (ObjectUtil.isNull(response) || ObjectUtil.isNull(response.getResult())) {
            return new ArrayList<>(0);
        }
        List<String> roleIds = response.getResult().stream().map(Role::getId).collect(Collectors.toList());
        roleCache.put(username, roleIds);
        return roleIds;
    }

    @Override
    public Boolean hasPermission(String username, String permission) {
        if (permissionCache.containsKey(username)) {
            Set<String> permissions = permissionCache.get(username);
            return permissions.contains(permission);
        }
        List<String> roleIds = getUserRoleIds(username);
        MarvelResponse<List<Permission>> response = feignBasisService.getPermissions(roleIds);
        Set<String> permissions = new HashSet<>(0);
        if (ObjectUtil.isNotNull(response) && ObjectUtil.isNotNull(response.getResult())) {
            for (Permission p : response.getResult()) {
                permissions.add(StrUtil.format(
                        "{} {}",
                        StrUtil.trimToEmpty(p.getMethod()),
                        StrUtil.trimToEmpty(p.getRoute())
                ));
            }
        }
        permissionCache.put(username, permissions);
        return permissions.contains(permission);
    }
}
