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

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.marvel.admin.entity.*;
import org.marvel.admin.service.MenuService;
import org.marvel.admin.service.PermissionService;
import org.marvel.admin.service.RoleService;
import org.marvel.admin.service.UserService;
import org.marvel.common.annotation.ResponseFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haoyuanqiang
 */
@RestController
@Slf4j
public class FrameworkController {

    @Resource
    private MenuService menuService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    /**
     * 获取菜单树
     *
     * @return 菜单树结构
     */
    @GetMapping("/framework/menus")
    @ResponseFormat
    public List<MenuEntity> getMenuTree(HttpServletRequest request) {
        String username = request.getHeader("username");

        try {
            if ("admin".equals(username)) {
                return filterMenus(menuService.getTree(), null);
            }
            UserLoginEntity user = userService.getUserLoginParam(username);
            List<RoleEntity> roles = roleService.getRolesByUserId(user.getId());
            List<String> menuIds = roleService.getRolesMenuIds(roles.stream()
                    .map(BaseEntity::getId).collect(Collectors.toList()));
            return filterMenus(menuService.getTree(), new HashSet<String>(menuIds));
        } catch (Exception e) {
            //
        }
        return new ArrayList<>(0);
    }

    /**
     * 过滤掉停用，隐藏，删除掉的菜单，如果目录下无子页面，也除去
     *
     * @param menus 原始菜单
     * @return 过滤后的菜单
     */
    private List<MenuEntity> filterMenus(List<MenuEntity> menus, Set<String> checkedMenuIds) {
        List<MenuEntity> result = new ArrayList<>();
        for (MenuEntity menu : menus) {
            if (CollectionUtil.isNotEmpty(menu.getChildren())) {
                menu.setChildren(filterMenus(menu.getChildren(), checkedMenuIds));
            }
            if (menu.getType() == MenuEntity.TYPE_PAGE
                    && menu.getValid() > 0
                    && menu.getVisible() == MenuEntity.VISIBLE_SHOW
                    && menu.getStatus() == MenuEntity.STATUS_ENABLE
                    && (checkedMenuIds == null || checkedMenuIds.contains(menu.getId()))
            ) {
                result.add(menu);
            } else if (menu.getType() == MenuEntity.TYPE_DIRECTORY
                    && CollectionUtil.isNotEmpty(menu.getChildren())) {
                result.add(menu);
            }
        }
        return result;
    }

    /**
     * 校验用户是否拥有菜单页面权限
     *
     * @param request 请求对象
     * @param params  参数
     * @return 校验结果
     */
    @PostMapping("/authorization/menu")
    @ResponseFormat
    public Map<String, Object> checkMenuPermission(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        String username = request.getHeader("username");
        String menuPath = MapUtil.getStr(params, "menu");
        Map<String, Object> result = new LinkedHashMap<>();
        String menuId = null;
        if (StrUtil.isNotEmpty(username) && StrUtil.isNotEmpty(menuPath)) {
            if (StrUtil.equals(username, "admin")) {
                result.put("isAuthorized", true);
            } else {
                UserLoginEntity loginEntity = userService.getUserLoginParam(username);
                if (ObjectUtil.isNotNull(loginEntity)) {
                    menuId = userService.checkUserMenuAvailability(loginEntity.getId(), menuPath);
                    if (StrUtil.isNotEmpty(menuId)) {
                        result.put("isAuthorized", true);
                    }
                }
            }
        }
        if (result.containsKey("isAuthorized")) {
            List<PermissionEntity> permissions = permissionService.getListByMenuId(menuId);
            result.put("permissions", CollectionUtil.map(permissions, item -> {
                item.setMenuId(null);
                item.setSortNumber(null);
                item.setModifyTime(null);
                item.setCreateTime(null);
                item.setValid(null);
                return item;
            }, true));
        } else {
            result.put("isAuthorized", false);
        }
        return result;
    }

    /**
     * 获取登陆用户信息
     *
     * @param request 请求对象
     * @return 登录用户信息
     */
    @GetMapping("/framework/login/user")
    @ResponseFormat
    public Map<String, Object> getLoginUser(HttpServletRequest request) {
        String username = request.getHeader("username");
        Map<String, Object> result = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(username)) {
            UserLoginEntity loginEntity = userService.getUserLoginParam(username);
            if (ObjectUtil.isNotNull(loginEntity)) {
                UserEntity entity = userService.get(loginEntity.getId());
                if (ObjectUtil.isNotNull(entity)) {
                    result.put(BaseEntity.Fields.id, entity.getId());
                    result.put(UserEntity.Fields.code, entity.getCode());
                    result.put(UserEntity.Fields.name, entity.getName());
                    result.put(UserEntity.Fields.loginName, entity.getLoginName());
                    result.put(UserEntity.Fields.nickname, entity.getNickname());
                    result.put(UserEntity.Fields.sex, entity.getSex());
                }
            }
        }
        return result;
    }


}
