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
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.marvel.admin.entity.BaseEntity;
import org.marvel.admin.entity.DepartmentEntity;
import org.marvel.admin.entity.UserEntity;
import org.marvel.admin.entity.UserLoginEntity;
import org.marvel.admin.service.DepartmentService;
import org.marvel.admin.service.UserService;
import org.marvel.common.annotation.ResponseFormat;
import org.marvel.common.exception.ExceptionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class PersonalController {

    @Resource
    private UserService userService;

    @Resource
    private DepartmentService departmentService;

    /**
     * 获取当前登录用户信息
     *
     * @param request Http请求
     * @return 用户信息
     */
    @GetMapping("/personal/info")
    @ResponseFormat
    public Map<String, Object> getPersonalInfo(HttpServletRequest request) {
        String username = request.getHeader("username");
        UserLoginEntity userLoginEntity = userService.getUserLoginParam(username);
        if (ObjectUtil.isNotNull(userLoginEntity)) {
            UserEntity user = userService.get(userLoginEntity.getId());
            if (ObjectUtil.isNotNull(user)) {
                List<String> keys = ListUtil.of(BaseEntity.Fields.id, UserEntity.Fields.name,
                        UserEntity.Fields.departmentId, UserEntity.Fields.nickname, UserEntity.Fields.sex,
                        UserEntity.Fields.email, UserEntity.Fields.telephone, BaseEntity.Fields.modifyTime);
                Map<String, Object> result = BeanUtil.beanToMap(user, new LinkedHashMap<>(), true, key -> {
                    return keys.contains(key) ? key : StrUtil.EMPTY;
                });
                DepartmentEntity departmentEntity = departmentService.get(user.getDepartmentId());
                if (ObjectUtil.isNotNull(departmentEntity)) {
                    result.put("departmentName", departmentEntity.getName());
                }
                return result;
            }
        }
        return new LinkedHashMap<>(0);
    }

    /**
     * 更新用户信息
     *
     * @param request    Http请求
     * @param parameters 请求参数
     *                   nickname：  用户昵称
     *                   email:      邮件地址
     *                   telephone： 手机号码
     */
    @PutMapping("/personal/info")
    @ResponseFormat
    public void updateUserInfo(HttpServletRequest request, @RequestBody Map<String, Object> parameters) {
        String username = request.getHeader("username");
        UserLoginEntity userLoginEntity = userService.getUserLoginParam(username);
        if (ObjectUtil.isNull(userLoginEntity)) {
            throw ExceptionFactory.create(1141);
        }
        UserEntity user = userService.get(userLoginEntity.getId());
        String nickname = MapUtil.getStr(parameters, UserEntity.Fields.nickname);
        if (StrUtil.isNotBlank(nickname)) {
            user.setNickname(nickname);
        }
        String email = MapUtil.getStr(parameters, UserEntity.Fields.email);
        if (StrUtil.isNotBlank(email)) {
            user.setEmail(email);
        }
        String telephone = MapUtil.getStr(parameters, UserEntity.Fields.telephone);
        if (StrUtil.isNotBlank(telephone)) {
            user.setTelephone(telephone);
        }
        userService.save(user);
    }

    /**
     * 更新用户密码
     *
     * @param request    Http请求
     * @param parameters 请求参数
     *                   oldPassword: 旧密码
     *                   newPassword: 新密码
     */
    @PutMapping("/personal/password")
    @ResponseFormat
    public void updatePersonalPassword(HttpServletRequest request, @RequestBody Map<String, Object> parameters) {
        String username = request.getHeader("username");
        UserLoginEntity userLoginEntity = userService.getUserLoginParam(username);
        if (ObjectUtil.isNull(userLoginEntity)) {
            throw ExceptionFactory.create(1141);
        }
        String oldPassword = MapUtil.getStr(parameters, "oldPassword");
        String newPassword = MapUtil.getStr(parameters, "newPassword");
        if (!userLoginEntity.isMatchPassword(oldPassword)) {
            throw ExceptionFactory.create(1146);
        }
        if (StrUtil.isBlank(newPassword)) {
            throw ExceptionFactory.create(1147);
        }
        log.info("UpdatePassword [{}, {}]", oldPassword, newPassword);
        // userService.updatePassword(userLoginEntity.getId(), newPassword);
    }
}
