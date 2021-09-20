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
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.admin.entity.BaseEntity;
import org.marvel.admin.entity.PositionEntity;
import org.marvel.admin.model.AcceptPosition;
import org.marvel.admin.service.PositionService;
import org.marvel.common.annotation.ResponseFormat;
import org.marvel.common.exception.ExceptionFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class PositionController {

    @Resource
    private PositionService positionService;

    /**
     * 获取岗位树
     *
     * @return 岗位树结构
     */
    @GetMapping("/positions/tree")
    @ResponseFormat
    public List<PositionEntity> getPositionTree() {
        return positionService.getTree();
    }

    /**
     * 获取岗位列表
     *
     * @return 全部岗位
     */
    @GetMapping("/positions")
    @ResponseFormat
    public List<AcceptPosition> getPositionList() {
        List<PositionEntity> entities = positionService.getList();
        return CollectionUtil.map(entities, item -> BeanUtil.copyProperties(
                item,
                AcceptPosition.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid,
                PositionEntity.Fields.children
        ), true);
    }


    /**
     * 获取岗位信息
     *
     * @param positionId 岗位ID
     * @return 岗位信息
     */
    @GetMapping("/position")
    @ResponseFormat
    public AcceptPosition getPosition(@RequestParam("id") String positionId) {
        PositionEntity position = positionService.get(positionId);
        return BeanUtil.copyProperties(
                position,
                AcceptPosition.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid,
                PositionEntity.Fields.children
        );
    }

    /**
     * 新增岗位
     *
     * @param position 岗位
     * @return 岗位ID
     */
    @PostMapping("/position")
    @ResponseFormat
    public String addPosition(@RequestBody AcceptPosition position) {
        int validateStatus = position.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (positionService.isDuplicate(position.getCode(), null, null)) {
            // duplicate department code
            throw ExceptionFactory.create(1124);
        }
        if (positionService.isDuplicate(null, position.getName(), null)) {
            // duplicate department name
            throw ExceptionFactory.create(1125);
        }
        if (StrUtil.isNotBlank(position.getParentId()) && !positionService.isExistent(position.getParentId())) {
            throw ExceptionFactory.create(1127);
        }
        PositionEntity entity = position.convertToEntity();
        entity.setId(null);
        positionService.save(entity);
        return entity.getId();
    }

    /**
     * 修改岗位
     *
     * @param position 岗位
     */
    @PutMapping("/position")
    @ResponseFormat
    public void updatePosition(@RequestBody AcceptPosition position) {
        int validateStatus = position.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (!positionService.isExistent(position.getId())) {
            // nonexistent entity
            throw ExceptionFactory.create(1126);
        }
        if (positionService.isDuplicate(position.getCode(), null, position.getId())) {
            // duplicate department code
            throw ExceptionFactory.create(1124);
        }
        if (positionService.isDuplicate(null, position.getName(), position.getId())) {
            // duplicate department name
            throw ExceptionFactory.create(1125);
        }
        if (StrUtil.isNotBlank(position.getParentId()) && !positionService.isExistent(position.getParentId())) {
            throw ExceptionFactory.create(1127);
        }
        PositionEntity entity = position.convertToEntity();
        positionService.save(entity);
    }

    /**
     * 软删除岗位
     *
     * @param positionIds 岗位ID列表
     */
    @PutMapping("/positions/invalidation")
    @ResponseFormat
    public int invalidatePositions(@RequestBody List<String> positionIds) {
        if (ObjectUtil.isNotNull(positionIds)) {
            return positionService.invalidate(positionIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个岗位
     *
     * @param positionId 岗位ID
     */
    @DeleteMapping("/position/{id}")
    @ResponseFormat
    public int deletePosition(@PathVariable("id") String positionId) {
        if (StrUtil.isNotEmpty(positionId)) {
            return positionService.delete(positionId);
        }
        return 0;
    }
}
