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

import org.marvel.admin.entity.PermissionEntity;
import org.marvel.admin.entity.RoleEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface RoleService extends BaseService<RoleEntity> {

    /**
     * 判断角色是否重复
     *
     * @param code       角色编码
     * @param name       角色名称
     * @param ignorantId 需忽略的部门 ID
     * @return 判断结果
     */
    Boolean isDuplicate(String code, String name, String ignorantId);

    /**
     * 查询全部角色（列表）
     *
     * @return 全部角色
     */
    List<RoleEntity> getList(int pageSize, int current, String code, String name);

    // --------------------------------------------------------------------- //
    // 角色权限操作接口
    // --------------------------------------------------------------------- //

    /**
     * 保存角色权限
     *
     * @param roleId      角色ID
     * @param permissions 权限ID集合
     */
    void saveRolePermissions(String roleId, Map<Integer, List<String>> permissions);

    /**
     * 获取用户菜单
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<String> getRoleMenuIds(String roleId);

    /**
     * 获取多个角色的菜单ID
     *
     * @param roleIds 角色ID
     * @return 菜单ID
     */
    List<String> getRolesMenuIds(List<String> roleIds);

    /**
     * 获取用户权限
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<String> getRolePermissionIds(String roleId);

    /**
     * 删除角色权限列表
     *
     * @param roleId 角色ID
     * @param type   权限或菜单
     * @return 删除数量
     */
    int deleteRolePermissions(String roleId, int type);

    /**
     * 获取角色权限
     *
     * @param roleIds 角色ID列表
     * @param types   类型
     * @return 角色权限列表
     */
    List<PermissionEntity> getRolePermissions(List<String> roleIds, List<Integer> types);


    // --------------------------------------------------------------------- //
    // 用户角色操作接口
    // --------------------------------------------------------------------- //

    /**
     * 根据用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleEntity> getRolesByUserId(String userId);
}
