package org.marvel.deevy.foundation.controller.authority;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.foundation.entity.security.Menu;
import org.marvel.deevy.foundation.param.MenuParam;
import org.marvel.deevy.foundation.service.security.MenuService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/3/6 19:37
 * @Copyright © 2016-2022 MARVEL
 */
@RestController
@RequestMapping("/protected/menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    /**
     * 获取全部菜单
     *
     * @return 全部菜单
     */
    @GetMapping("/list")
    public List<MenuParam> getMenuList() {
        List<Menu> menus = menuService.getAll();
        return CollectionUtil.map(menus, item -> BeanUtil.copyProperties(
                item,
                MenuParam.class
        ), true);
    }


    /**
     * 获取菜单
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @GetMapping
    public MenuParam getMenu(@RequestParam("id") String menuId) {
        Menu menu = menuService.getById(menuId);
        return BeanUtil.copyProperties(menu, MenuParam.class);
    }

    /**
     * 新增角色
     *
     * @param menu 角色
     * @return 菜单ID
     */
    @PostMapping
    public String addMenu(@RequestBody MenuParam menu) {
        int validateStatus = menu.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (menuService.isDuplicate(menu.getCode(), null, null)) {
            // duplicate menu code
            throw ExceptionFactory.create(1114);
        }
        if (menuService.isDuplicate(null, menu.getName(), null)) {
            // duplicate menu name
            throw ExceptionFactory.create(1115);
        }
        Menu entity = new Menu();
        BeanUtil.copyProperties(menu, entity, BaseEntity.Fields.createTime, BaseEntity.Fields.valid);
        entity.setId(null);
        menuService.save(entity);
        return entity.getId();
    }

    /**
     * 修改菜单
     *
     * @param menu 菜单
     */
    @PutMapping
    public void updateMenu(@RequestBody MenuParam menu) {
        int validateStatus = menu.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        Menu entity = menuService.getById(menu.getId());
        if (null == entity) {
            // nonexistent entity
            throw ExceptionFactory.create(1116);
        }
        if (menuService.isDuplicate(menu.getCode(), null, menu.getId())) {
            // duplicate menu code
            throw ExceptionFactory.create(1114);
        }
        if (menuService.isDuplicate(null, menu.getName(), menu.getId())) {
            // duplicate menu name
            throw ExceptionFactory.create(1115);
        }
        BeanUtil.copyProperties(menu, entity, CopyOptions.create().ignoreNullValue().setIgnoreProperties(
                BaseEntity.Fields.createTime, BaseEntity.Fields.modifyTime));
        BeanUtil.copyProperties(menu, entity);
        menuService.save(entity);
    }

    /**
     * 软删除菜单
     *
     * @param menuIds 菜单ID列表
     */
    @PutMapping("/invalidation")
    public int invalidateMenus(@RequestBody List<String> menuIds) {
        if (ObjectUtil.isNotNull(menuIds)) {

            return menuService.invalidate(menuIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个菜单
     *
     * @param menuIds 菜单ID
     */
    @DeleteMapping("/{ids}")
    public int deleteMenu(@PathVariable("ids") List<String> menuIds) {
        if (CollectionUtil.isNotEmpty(menuIds)) {

            return menuService.delete(menuIds);
        }
        return 0;
    }
}
