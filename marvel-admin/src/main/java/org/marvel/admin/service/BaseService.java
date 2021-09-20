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

import org.marvel.admin.entity.BaseEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface BaseService<EntityType extends BaseEntity> {
    /**
     * 保存实体信息
     *
     * @param entity 实体信息
     * @return 实体信息
     */
    EntityType save(EntityType entity);

    /**
     * 批量保存实体信息
     *
     * @param entities 实体列表
     * @return 实体列表
     */
    List<EntityType> saveAll(List<EntityType> entities);

    /**
     * 使实体失效
     *
     * @param id 实体 ID
     * @return 删除成功的实体数量
     */
    int invalidate(String id);

    /**
     * 批量使实体失效
     *
     * @param ids 实体 ID 列表
     * @return 删除成功的实体数量
     */
    int invalidate(List<String> ids);

    /**
     * 自定义条件使实体失效
     *
     * @param specification Specification in the sense of Domain Driven Design
     *                      参数:
     *                      root – must not be null.
     *                      query – should not use because it is always null.
     *                      criteriaBuilder – must not be null.
     *                      返回值:
     *                      a Predicate, may be null.
     */
    int invalidate(Specification<EntityType> specification);

    /**
     * 删除实体（硬删除）
     *
     * @param id 实体 ID
     * @return 删除成功的实体数量
     */
    int delete(String id);

    /**
     * 批量删除实体（硬删除）
     *
     * @param ids 实体 ID 列表
     * @return 删除成功的实体数量
     */
    int delete(List<String> ids);

    /**
     * 自定义条件删除实体
     *
     * @param specification Specification in the sense of Domain Driven Design
     *                      参数:
     *                      root – must not be null.
     *                      query – should not use because it is always null.
     *                      criteriaBuilder – must not be null.
     *                      返回值:
     *                      a Predicate, may be null.
     */
    int delete(Specification<EntityType> specification);

    /**
     * 判断实体是否存在
     *
     * @param id 实体ID
     * @return 判断结果
     */
    Boolean isExistent(String id);

    /**
     * 根据 ID 获取实体信息
     *
     * @param id 实体 ID
     * @return 实体信息
     */
    EntityType get(String id);
}
