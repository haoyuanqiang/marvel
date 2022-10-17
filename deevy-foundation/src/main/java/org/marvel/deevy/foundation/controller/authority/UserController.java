package org.marvel.deevy.foundation.controller.authority;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.common.model.PageResult;
import org.marvel.deevy.common.model.Pagination;
import org.marvel.deevy.common.model.Pair;
import org.marvel.deevy.foundation.entity.organization.Staff;
import org.marvel.deevy.foundation.entity.security.Role;
import org.marvel.deevy.foundation.entity.security.UserGroup;
import org.marvel.deevy.foundation.param.UserParam;
import org.marvel.deevy.foundation.service.association.UserAndRoleAssociationService;
import org.marvel.deevy.foundation.service.association.UserGroupAndUserAssociationService;
import org.marvel.deevy.foundation.service.organization.DepartmentService;
import org.marvel.deevy.foundation.service.organization.StaffService;
import org.marvel.deevy.foundation.service.security.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/1/3 20:34
 * @Copyright © 2016-2022 MARVEL
 */
@RestController
@RequestMapping("/protected/user")
public class UserController {

    @Resource
    private StaffService staffService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private RoleService roleService;


    @Autowired
    private UserAndRoleAssociationService userAndRoleAssociationService;


    @Autowired
    private UserGroupAndUserAssociationService userGroupAndUserAssociationService;


    // -------------------------------------------------------------------------------------------------------------- //
    // 用户信息 CRUD 接口
    // -------------------------------------------------------------------------------------------------------------- //

    /**
     * 获取用户列表
     *
     * @param pageSize 分页大小
     * @param current  当前页码
     * @return 用户列表
     */
    @GetMapping("/page")
    public PageResult<UserParam> getList(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String codeOrName
    ) {
        PageResult<Staff> pageData = staffService.getList(
                ObjectUtil.defaultIfNull(pageSize, 20),
                ObjectUtil.defaultIfNull(current, 1),
                departmentId,
                codeOrName
        );
        Pagination pagination = pageData.getPagination();
        while (CollectionUtil.isEmpty(pageData.getList()) && pagination.getCurrent() > 1) {
            pageData = staffService.getList(pagination.getPageSize(), pagination.getCurrent() - 1, departmentId, codeOrName);
            pagination = pageData.getPagination();
        }

        List<UserParam> list = CollectionUtil.map(pageData.getList(), (item) -> {
            return BeanUtil.copyProperties(item, UserParam.class, BaseEntity.Fields.createTime, BaseEntity.Fields.valid);
        }, true);
        return new PageResult<>(list, pagination.getPageSize(), pagination.getCurrent(), pagination.getTotal());
    }

    /**
     * 获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping
    public UserParam getUser(@RequestParam String id) {
        Staff entity = staffService.getById(id);
        if (ObjectUtil.isNull(entity)) {
            throw ExceptionFactory.create(1141);
        }
        UserParam user = BeanUtil.copyProperties(
                entity,
                UserParam.class
        );
        return user;
    }

    /**
     * 新增用户
     *
     * @param user 用户
     * @return 用户ID
     */
    @PostMapping
    public String addUser(@RequestBody UserParam user) {
        int validateStatus = user.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (staffService.isDuplicate(MapUtil.of(Staff.Fields.code, user.getCode()), null)) {
            // duplicate user code
            throw ExceptionFactory.create(1142);
        }
        if (staffService.isDuplicate(MapUtil.of(Staff.Fields.name, user.getName()), null)) {
            // duplicate user name
            throw ExceptionFactory.create(1143);
        }
//        if (staffService.isDuplicate(MapUtil.of(Staff.Fields.loginName, user.getLoginName()), null)) {
//            throw ExceptionFactory.create(1144);
//        }
        if (staffService.isDuplicate(MapUtil.of(Staff.Fields.nickname, user.getNickname()), null)) {
            throw ExceptionFactory.create(1145);
        }
        validate(user);
        Staff entity = BeanUtil.copyProperties(user, Staff.class, BaseEntity.Fields.createTime);
        entity.setId(null);
        entity.setReadOnly(false);
        staffService.save(entity);
        return entity.getId();
    }

    /**
     * 修改用户
     *
     * @param user 用户
     */
    @PutMapping
    public void updateUser(@RequestBody UserParam user) {
        int validateStatus = user.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        Staff entity = staffService.getById(user.getId());
        if (null == entity) {
            throw ExceptionFactory.create(1141);
        }
        if (staffService.isDuplicate(MapUtil.of(Staff.Fields.code, user.getCode()),
                MapUtil.of(BaseEntity.Fields.id, user.getId()))) {
            // duplicate user code
            throw ExceptionFactory.create(1142);
        }
        if (staffService.isDuplicate(MapUtil.of(Staff.Fields.name, user.getName()),
                MapUtil.of(BaseEntity.Fields.id, user.getId()))) {
            // duplicate user name
            throw ExceptionFactory.create(1143);
        }
//        if (staffService.isDuplicate(MapUtil.of(Staff.Fields.loginName, user.getLoginName()),
//                MapUtil.of(BaseEntity.Fields.id, user.getId()))) {
//            throw ExceptionFactory.create(1144);
//        }
        if (staffService.isDuplicate(MapUtil.of(Staff.Fields.nickname, user.getNickname()),
                MapUtil.of(BaseEntity.Fields.id, user.getId()))) {
            throw ExceptionFactory.create(1145);
        }
        validate(user);
        BeanUtil.copyProperties(user, entity, BaseEntity.Fields.createTime, BaseEntity.Fields.valid);
        staffService.save(entity);
    }

    /**
     * 校验用户
     *
     * @param user 用户信息
     */
    private void validate(UserParam user) {
        if (!departmentService.isExistent(user.getDepartmentId())) {
            throw ExceptionFactory.create(1138);
        }
        List<String> roleIds = user.getRoleIds();
        if (ObjectUtil.isNull(roleIds) && CollectionUtil.isNotEmpty(roleIds)) {
            for (String roleId : roleIds) {
                if (!roleService.isExistent(roleId)) {
                    throw ExceptionFactory.create(1139);
                }
            }
        }
        List<String> positionIds = user.getPositionIds();
        if (ObjectUtil.isNull(positionIds) && CollectionUtil.isNotEmpty(positionIds)) {
            for (String positionId : positionIds) {
                if (!roleService.isExistent(positionId)) {
                    throw ExceptionFactory.create(1140);
                }
            }
        }
    }

    /**
     * 软删除用户
     *
     * @param userIds 用户ID列表
     */
    @PutMapping("/invalidation")
    public int invalidateUsers(@RequestBody List<String> userIds) {
        if (ObjectUtil.isNotNull(userIds)) {
            return staffService.delete(userIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个用户
     *
     * @param userIds 用户ID
     */
    @DeleteMapping("/{ids}")
    public int deleteUser(@PathVariable("ids") List<String> userIds) {
        if (CollectionUtil.isNotEmpty(userIds)) {
            return staffService.delete(userIds);
        }
        return 0;
    }


    // -------------------------------------------------------------------------------------------------------------- //
    // 用户信息 —— 操作角色
    // -------------------------------------------------------------------------------------------------------------- //

    /**
     * 获取用户关联角色的ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @GetMapping("/roleIds")
    public List<String> queryRoleIdsByUserId(@RequestParam String userId) {
        List<Role> roles = userAndRoleAssociationService.getRolesByUserId(userId);
        return CollectionUtil.map(roles, BaseEntity::getId, true);
    }

    /**
     * 获取用户关联角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/roles")
    public List<Role> queryRolesByUserId(@RequestParam String userId) {
        return userAndRoleAssociationService.getRolesByUserId(userId);
    }


    /**
     * 新建用户关联角色信息
     *
     * @param associations 关联关系，Key表示用户ID， Value表示角色ID
     * @return 更新数量
     */
    @PostMapping("/roles")
    public int createUserRoles(@RequestBody List<Pair<String, String>> associations) {
        int count = 0;
        if (null != associations) {
            for (Pair<String, String> association : associations) {
                userAndRoleAssociationService.save(association.getKey(), association.getValue());
                count++;
            }
        }
        return count;
    }


    /**
     * 更新用户关联角色信息，先清除原来的关系，新建新的
     *
     * @param associations 关联关系，Key表示用户ID， Value表示角色ID
     * @return 更新数量
     */
    @PutMapping("/roles")
    public int updateUserRoles(@RequestBody List<Pair<String, String>> associations) {
        int count = 0;

        if (null != associations && associations.size() > 0) {
            HashSet<String> userIds = new HashSet<>(8);
            for (Pair<String, String> association : associations) {
                userIds.add(association.getKey());
            }
            userAndRoleAssociationService.deleteAssociationByUserId(new ArrayList<>(userIds));
            for (Pair<String, String> association : associations) {
                userAndRoleAssociationService.save(association.getKey(), association.getValue());
                count++;
            }
        }
        return count;
    }

    /**
     * 删除指定的用户和角色关联关系
     *
     * @param associations 关系
     * @return 删除成功数量
     */
    @DeleteMapping("/roles")
    public int deleteUserRoles(@RequestBody List<Pair<String, String>> associations) {
        int count = 0;
        if (null != associations) {
            for (Pair<String, String> association : associations) {
                userAndRoleAssociationService.save(association.getKey(), association.getValue());
                count++;
            }
        }
        return count;
    }


    // -------------------------------------------------------------------------------------------------------------- //
    // 用户信息 —— 操作岗位
    // -------------------------------------------------------------------------------------------------------------- //

    @GetMapping("/userGroupIds")
    public List<String> queryUserGroupIdsByUserId(@RequestParam String userId) {
        List<UserGroup> userGroups = userGroupAndUserAssociationService.getUserGroupsByUserId(userId);
        return CollectionUtil.map(userGroups, BaseEntity::getId, true);
    }


    @GetMapping("/userGroups")
    public List<UserGroup> queryUserGroupsByUserId(@RequestParam String userId) {
        return userGroupAndUserAssociationService.getUserGroupsByUserId(userId);
    }


    /**
     * 新建用户关联角色信息
     *
     * @param associations 关联关系，Key表示用户ID， Value表示用户分组ID
     * @return 更新数量
     */
    @PostMapping("/userGroups")
    public int createUserGroupAssociation(@RequestBody List<Pair<String, String>> associations) {
        int count = 0;
        if (null != associations) {
            for (Pair<String, String> association : associations) {
                userGroupAndUserAssociationService.save(association.getValue(), association.getKey());
                count++;
            }
        }
        return count;
    }


    /**
     * 更新用户关联角色信息，先清除原来的关系，新建新的
     *
     * @param associations 关联关系，Key表示用户ID，Value表示用户组ID
     * @return 更新数量
     */
    @PutMapping("/userGroups")
    public int updateUserGroupAssociation(@RequestBody List<Pair<String, String>> associations) {
        int count = 0;
        if (null != associations && associations.size() > 0) {
            HashSet<String> userIds = new HashSet<>(8);
            for (Pair<String, String> association : associations) {
                userIds.add(association.getKey());
            }
            userGroupAndUserAssociationService.deleteAssociationByUserId(new ArrayList<>(userIds));
            for (Pair<String, String> association : associations) {
                userGroupAndUserAssociationService.save(association.getValue(), association.getKey());
                count++;
            }
        }
        return count;
    }

    /**
     * 删除指定的用户和角色关联关系
     *
     * @param associations 关系,key表示用户ID，value表示用户组ID
     * @return 删除成功数量
     */
    @DeleteMapping("/userGroups")
    public int deleteUserGroups(@RequestBody List<Pair<String, String>> associations) {
        int count = 0;
        if (null != associations) {
            for (Pair<String, String> association : associations) {
                userGroupAndUserAssociationService.delete(association.getValue(), association.getKey());
                count++;
            }
        }
        return count;
    }

}
