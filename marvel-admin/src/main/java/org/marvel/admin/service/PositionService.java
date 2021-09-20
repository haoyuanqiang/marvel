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

import org.marvel.admin.entity.PositionEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PositionService extends BaseService<PositionEntity> {

    /**
     * 判断岗位是否重复
     *
     * @param code       岗位编码
     * @param name       岗位名称
     * @param ignorantId 需忽略的岗位ID
     * @return 判断结果
     */
    Boolean isDuplicate(String code, String name, String ignorantId);

    /**
     * 获取全部岗位
     *
     * @return 全部岗位
     */
    List<PositionEntity> getList();

    /**
     * 获取岗位树
     *
     * @return 岗位树
     */
    List<PositionEntity> getTree();
}
