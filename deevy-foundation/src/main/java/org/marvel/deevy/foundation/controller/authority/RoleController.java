package org.marvel.deevy.foundation.controller.authority;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.common.model.PageResult;
import org.marvel.deevy.common.model.Pagination;
import org.marvel.deevy.foundation.entity.security.Role;
import org.marvel.deevy.foundation.param.RoleParam;
import org.marvel.deevy.foundation.service.security.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/1/3 14:26
 * @Copyright © 2016-2022 MARVEL
 */
@RestController
@RequestMapping("/protected/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 获取角色列表
     *
     * @return 全部角色
     */
    @GetMapping("/page")
    public PageResult<RoleParam> getRolePage(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name
    ) {
        PageResult<Role> pageData = roleService.getList(
                ObjectUtil.defaultIfNull(pageSize, 20),
                ObjectUtil.defaultIfNull(current, 1),
                code,
                name
        );
        Pagination pagination = pageData.getPagination();
        while (CollectionUtil.isEmpty(pageData.getList()) && pagination.getCurrent() > 1) {
            pageData = roleService.getList(pagination.getPageSize(), pagination.getCurrent() - 1, code, name);
            pagination = pageData.getPagination();
        }
        List<RoleParam> list = CollectionUtil.map(pageData.getList(), item -> BeanUtil.copyProperties(
                item,
                RoleParam.class
        ), true);
        return new PageResult<>(list, pagination.getPageSize(), pagination.getCurrent(), pagination.getTotal());
    }

    @GetMapping("/list")
    public List<RoleParam> getRoleList() {
        return CollectionUtil.map(roleService.getAll(), item -> BeanUtil.copyProperties(
                item,
                RoleParam.class
        ), true);
    }

    /**
     * 获取角色
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    @GetMapping
    public RoleParam getRole(@RequestParam("id") String roleId) {
        Role role = roleService.getById(roleId);
        return BeanUtil.copyProperties(role, RoleParam.class);
    }

    /**
     * 新增角色
     *
     * @param role 角色
     * @return 角色ID
     */
    @PostMapping
    public String addRole(@RequestBody RoleParam role) {
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
        Role entity = new Role();
        BeanUtil.copyProperties(role, entity, BaseEntity.Fields.createTime, BaseEntity.Fields.valid);
        entity.setId(null);
        roleService.save(entity);
        return entity.getId();
    }

    /**
     * 修改角色
     *
     * @param role 角色
     */
    @PutMapping
    public void updateRole(@RequestBody RoleParam role) {
        int validateStatus = role.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        Role entity = roleService.getById(role.getId());
        if (null == entity) {
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
        BeanUtil.copyProperties(role, entity, CopyOptions.create().ignoreNullValue().setIgnoreProperties(
                BaseEntity.Fields.createTime, BaseEntity.Fields.modifyTime));
        BeanUtil.copyProperties(role, entity);
        roleService.save(entity);
    }

    /**
     * 软删除角色
     *
     * @param roleIds 角色ID列表
     */
    @PutMapping("/invalidation")
    public int invalidateRoles(@RequestBody List<String> roleIds) {
        if (ObjectUtil.isNotNull(roleIds)) {
//            for (String roleId : roleIds) {
//                roleService.delete(roleId, 0);
//            }
            return roleService.invalidate(roleIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个角色
     *
     * @param roleIds 角色ID
     */
    @DeleteMapping("/{ids}")
    public int deleteRole(@PathVariable("ids") List<String> roleIds) {
        if (CollectionUtil.isNotEmpty(roleIds)) {
//            roleService.deleteRolePermissions(roleId, 0);
            return roleService.delete(roleIds);
        }
        return 0;
    }
}
