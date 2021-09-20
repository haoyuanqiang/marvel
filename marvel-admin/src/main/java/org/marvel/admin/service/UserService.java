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

package org.marvel.admin.service;

import org.marvel.admin.entity.UserEntity;
import org.marvel.admin.entity.UserLoginEntity;
import org.marvel.admin.model.Pagination;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UserService extends BaseService<UserEntity> {
    // ---------------------------------------------------------------------//
    // 用户管理接口
    // ---------------------------------------------------------------------//

    /**
     * 查询用户列表
     *
     * @param pageSize        分页大小
     * @param current         当前分页
     * @param otherConditions 其他查询条件
     * @return 用户列表
     */
    Pagination<UserEntity> getList(int pageSize, int current, Map<String, Object> otherConditions);

    /**
     * 判断实体属性是否重复
     *
     * @param matches   匹配的属性
     * @param ignorance 忽略的属性
     * @return 判断结果
     */
    Boolean isDuplicate(Map<String, Object> matches, Map<String, Object> ignorance);

    /**
     * 保存用户，不要直接使用save
     *
     * @param entity 用户实体
     * @return 用户实体
     */
    UserEntity saveUser(UserEntity entity);

    /**
     * 删除用户，不要使用delete
     *
     * @param ids 用户ID
     * @return 删除数量
     */
    int deleteUser(List<String> ids);

    /**
     * 删除用户，不要使用deleteFully
     *
     * @param ids 用户ID
     * @return 删除数量
     */
    int deleteUserFully(List<String> ids);


    // ---------------------------------------------------------------------//
    // 用户登录接口
    // ---------------------------------------------------------------------//

    /**
     * 根据登录名称获取用户密码，用于登录匹配
     *
     * @param loginName 登录名称
     * @return 用户登录参数
     */
    UserLoginEntity getUserLoginParam(String loginName);

    /**
     * 更新用户登录密码
     *
     * @param id          用户ID
     * @param newPassword 新的密码
     */
    int updatePassword(String id, String newPassword);

    // ----------------------------------------------------------------------- //
    // 用户角色关联关系操作
    // ----------------------------------------------------------------------- //

    /**
     * 保存用户关联的角色ID
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void saveUserRoleIds(String userId, List<String> roleIds);

    /**
     * 获取用户关联的角色ID
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<String> getUserRoleIds(String userId);

    /**
     * 删除用户关联角色
     *
     * @param userId 用户ID
     * @return 删除数量
     */
    int deleteUserRoleIds(String userId);

    /**
     * 检查用户是否拥有指定菜单页面权限
     *
     * @param userId   用户ID
     * @param menuPath 菜单路径
     * @return Menu ID
     */
    String checkUserMenuAvailability(String userId, String menuPath);

    // ----------------------------------------------------------------------- //
    // 用户岗位关联关系操作
    // ----------------------------------------------------------------------- //

    /**
     * 保存用户关联的岗位ID
     *
     * @param userId      用户ID
     * @param positionIds 岗位ID列表
     */
    void saveUserPositionIds(String userId, List<String> positionIds);

    /**
     * 获取用户关联的岗位ID列表
     *
     * @param userId 用户ID
     * @return 岗位ID列表
     */
    List<String> getUserPositionIds(String userId);

    /**
     * 删除用户关联岗位
     *
     * @param userId 用户ID
     * @return 删除数量
     */
    int deleteUserPositionIds(String userId);
}
