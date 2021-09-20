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

import org.marvel.admin.entity.MenuEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MenuService extends BaseService<MenuEntity> {
    /**
     * 判断部门是否重复
     *
     * @param code       部门编码
     * @param name       部门名称
     * @param ignorantId 需忽略的部门 ID
     * @return 判断结果
     */
    Boolean isDuplicate(String code, String name, String ignorantId);

    /**
     * 获取全部部门（列表形式）
     *
     * @return 部门列表
     */
    List<MenuEntity> getList();

    /**
     * 获取全部部门（部门树形式）
     *
     * @return 部门树
     */
    List<MenuEntity> getTree();
}
