package org.marvel.deevy.foundation.controller.framework;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import org.marvel.deevy.foundation.entity.security.Menu;
import org.marvel.deevy.foundation.param.MenuParam;
import org.marvel.deevy.foundation.service.security.MenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haoyuanqiang
 * @date 2022/3/17 22:04
 * @Copyright © 2016-2022 MARVEL
 */
@RestController
@RequestMapping("/protected/framework")
public class FrameworkController {

    @Resource
    private MenuService menuService;

    @GetMapping("/menus")
    public List<MenuParam> getAuthorizedMenus() {
        List<Menu> menus = menuService.getAll();
        return CollectionUtil.map(menus, item -> BeanUtil.copyProperties(
                item,
                MenuParam.class
        ), true);
    }

    @GetMapping("/currentUser")
    public Map<String, Object> getCurrentUser() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", "10001");
        userInfo.put("name", "admin");
        userInfo.put("loginName", "admin");
        userInfo.put("code", "admin");
        userInfo.put("nickname", "admin");
        return userInfo;
    }

}
