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
import org.marvel.admin.entity.MenuEntity;
import org.marvel.admin.model.AcceptMenu;
import org.marvel.admin.service.MenuService;
import org.marvel.common.annotation.ResponseFormat;
import org.marvel.common.exception.ExceptionFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class MenuController {

    @Resource
    private MenuService menuService;

    /**
     * 获取菜单树
     *
     * @return 菜单树结构
     */
    @GetMapping("/menus/tree")
    @ResponseFormat
    public List<MenuEntity> getMenuTree() {
        return menuService.getTree();
    }

    /**
     * 获取菜单列表
     *
     * @return 全部菜单
     */
    @GetMapping("/menus")
    @ResponseFormat
    public List<AcceptMenu> getMenuList() {
        List<MenuEntity> entities = menuService.getList();
        return CollectionUtil.map(entities, item -> BeanUtil.copyProperties(
                item,
                AcceptMenu.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid,
                MenuEntity.Fields.children
        ), true);
    }

    /**
     * 获取菜单
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @GetMapping("/menu")
    @ResponseFormat
    public AcceptMenu getMenu(@RequestParam("id") String menuId) {
        MenuEntity menu = menuService.get(menuId);
        if (ObjectUtil.isNull(menu)) {
            throw ExceptionFactory.create(1156);
        }
        return BeanUtil.copyProperties(
                menu,
                AcceptMenu.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid,
                MenuEntity.Fields.children
        );
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单
     * @return 菜单ID
     */
    @PostMapping("/menu")
    @ResponseFormat
    public String addMenu(@RequestBody AcceptMenu menu) {
        int validateStatus = menu.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (menuService.isDuplicate(menu.getCode(), null, null)) {
            // duplicate menu code
            throw ExceptionFactory.create(1154);
        }
        if (menuService.isDuplicate(null, menu.getName(), null)) {
            // duplicate menu name
            throw ExceptionFactory.create(1155);
        }
        MenuEntity entity = BeanUtil.copyProperties(
                menu,
                MenuEntity.class,
                MenuEntity.Fields.children
        );
        entity.setId(null);
        menuService.save(entity);
        return entity.getId();
    }

    /**
     * 修改菜单
     *
     * @param menu 菜单
     */
    @PutMapping("/menu")
    @ResponseFormat
    public void updateMenu(@RequestBody AcceptMenu menu) {
        int validateStatus = menu.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (!menuService.isExistent(menu.getId())) {
            // nonexistent entity
            throw ExceptionFactory.create(1156);
        }
        if (menuService.isDuplicate(menu.getCode(), null, menu.getId())) {
            // duplicate menu code
            throw ExceptionFactory.create(1154);
        }
        if (menuService.isDuplicate(null, menu.getName(), menu.getId())) {
            // duplicate menu name
            throw ExceptionFactory.create(1155);
        }

        MenuEntity entity = menuService.get(menu.getId());
        BeanUtil.copyProperties(
                menu,
                entity,
                MenuEntity.Fields.children
        );
        menuService.save(entity);
    }

    /**
     * 软删除菜单
     *
     * @param menuIds 菜单ID列表
     */
    @PutMapping("/menus/invalidation")
    @ResponseFormat
    public int invalidateMenus(@RequestBody List<String> menuIds) {
        if (ObjectUtil.isNotNull(menuIds)) {
            return menuService.invalidate(menuIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个菜单
     *
     * @param menuId 菜单ID
     */
    @DeleteMapping("/menu/{id}")
    @ResponseFormat
    public int deleteMenu(@PathVariable("id") String menuId) {
        if (StrUtil.isNotEmpty(menuId)) {
            return menuService.delete(menuId);
        }
        return 0;
    }
}
