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
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.marvel.admin.entity.BaseEntity;
import org.marvel.admin.entity.UserEntity;
import org.marvel.admin.model.AcceptUser;
import org.marvel.admin.model.Pagination;
import org.marvel.admin.service.DepartmentService;
import org.marvel.admin.service.PositionService;
import org.marvel.admin.service.RoleService;
import org.marvel.admin.service.UserService;
import org.marvel.common.annotation.ResponseFormat;
import org.marvel.common.exception.ExceptionFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haoyuanqiang
 */
@RestController
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private PositionService positionService;

    @Resource
    private RoleService roleService;

    /**
     * 获取用户列表
     *
     * @param pageSize 分页大小
     * @param current  当前页码
     * @param name     用户名称，模糊搜索
     * @return 用户列表
     */
    @GetMapping("/users")
    @ResponseFormat
    public Pagination<AcceptUser> getList(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String name
    ) {
        Map<String, Object> otherConditions = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(name)) {
            otherConditions.put("name", name);
        }
        if (StrUtil.isNotBlank(departmentId)) {
            otherConditions.put("departmentId", departmentId);
        }
        Pagination<UserEntity> pagination = userService.getList(
                ObjectUtil.defaultIfNull(pageSize, 20),
                ObjectUtil.defaultIfNull(current, 1),
                otherConditions
        );
        List<UserEntity> list = pagination.getList();
        List<AcceptUser> users = CollectionUtil.map(list, (item) -> {
            return BeanUtil.copyProperties(item, AcceptUser.class, BaseEntity.Fields.createTime, BaseEntity.Fields.valid);
        }, true);
        return new Pagination<>(users, pagination.getTotal(), pagination.getPageSize(), pagination.getCurrent());
    }

    /**
     * 获取用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/user")
    @ResponseFormat
    public AcceptUser getUser(@RequestParam String id) {
        UserEntity entity = userService.get(id);
        if (ObjectUtil.isNull(entity)) {
            throw ExceptionFactory.create(1141);
        }
        AcceptUser user = BeanUtil.copyProperties(
                entity,
                AcceptUser.class
        );
        user.setRoleIds(userService.getUserRoleIds(id));
        user.setPositionIds(userService.getUserPositionIds(id));
        return user;
    }

    /**
     * 新增用户
     *
     * @param user 用户
     * @return 用户ID
     */
    @PostMapping("/user")
    @ResponseFormat
    public String addUser(@RequestBody AcceptUser user) {
        int validateStatus = user.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (userService.isDuplicate(MapUtil.of(UserEntity.Fields.code, user.getCode()), null)) {
            // duplicate user code
            throw ExceptionFactory.create(1142);
        }
        if (userService.isDuplicate(MapUtil.of(UserEntity.Fields.name, user.getName()), null)) {
            // duplicate user name
            throw ExceptionFactory.create(1143);
        }
        if (userService.isDuplicate(MapUtil.of(UserEntity.Fields.loginName, user.getLoginName()), null)) {
            throw ExceptionFactory.create(1144);
        }
        if (userService.isDuplicate(MapUtil.of(UserEntity.Fields.nickname, user.getNickname()), null)) {
            throw ExceptionFactory.create(1145);
        }
        validate(user);
        UserEntity entity = new UserEntity();
        BeanUtil.copyProperties(user, entity);
        entity.setId(null);
        entity.setReadOnly(false);
        userService.saveUser(entity);
        userService.saveUserRoleIds(entity.getId(), user.getRoleIds());
        userService.saveUserPositionIds(entity.getId(), user.getPositionIds());
        return entity.getId();
    }

    /**
     * 修改用户
     *
     * @param user 用户
     */
    @PutMapping("/user")
    @ResponseFormat
    public void updateUser(@RequestBody AcceptUser user) {
        int validateStatus = user.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (userService.isDuplicate(MapUtil.of(UserEntity.Fields.code, user.getCode()),
                MapUtil.of(BaseEntity.Fields.id, user.getId()))) {
            // duplicate user code
            throw ExceptionFactory.create(1142);
        }
        if (userService.isDuplicate(MapUtil.of(UserEntity.Fields.name, user.getName()),
                MapUtil.of(BaseEntity.Fields.id, user.getId()))) {
            // duplicate user name
            throw ExceptionFactory.create(1143);
        }
        if (userService.isDuplicate(MapUtil.of(UserEntity.Fields.loginName, user.getLoginName()),
                MapUtil.of(BaseEntity.Fields.id, user.getId()))) {
            throw ExceptionFactory.create(1144);
        }
        if (userService.isDuplicate(MapUtil.of(UserEntity.Fields.nickname, user.getNickname()),
                MapUtil.of(BaseEntity.Fields.id, user.getId()))) {
            throw ExceptionFactory.create(1145);
        }
        validate(user);
        UserEntity entity = userService.get(user.getId());
        BeanUtil.copyProperties(user, entity);
        userService.saveUser(entity);
        userService.saveUserRoleIds(entity.getId(), user.getRoleIds());
        userService.saveUserPositionIds(entity.getId(), user.getPositionIds());
    }

    /**
     * 校验用户
     *
     * @param user 用户信息
     */
    private void validate(AcceptUser user) {
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
    @PutMapping("/users/invalidation")
    @ResponseFormat
    public int invalidateUsers(@RequestBody List<String> userIds) {
        if (ObjectUtil.isNotNull(userIds)) {
            for (String userId : userIds) {
                userService.deleteUserRoleIds(userId);
                userService.deleteUserPositionIds(userId);
            }
            return userService.deleteUser(userIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个用户
     *
     * @param userId 用户ID
     */
    @DeleteMapping("/user/{id}")
    @ResponseFormat
    public int deleteUser(@PathVariable("id") String userId) {
        if (StrUtil.isNotEmpty(userId)) {
            userService.deleteUserRoleIds(userId);
            userService.deleteUserPositionIds(userId);
            return userService.deleteUserFully(ListUtil.toList(userId));
        }
        return 0;
    }

    /**
     * 修改用户密码
     *
     * @param parameters 参数
     */
    @PostMapping("/user/password")
    @ResponseFormat
    public void updateUserPassword(@RequestBody JSONObject parameters) {
        String userId = parameters.getString("userId");
        String password = parameters.getString("password");
        if (userService.isExistent(userId) && StrUtil.isNotBlank(password)) {
            userService.updatePassword(userId, StrUtil.trim(password));
        }
    }
}
