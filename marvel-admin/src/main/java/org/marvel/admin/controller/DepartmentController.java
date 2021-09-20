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
import org.marvel.admin.entity.DepartmentEntity;
import org.marvel.admin.model.AcceptDepartment;
import org.marvel.admin.service.DepartmentService;
import org.marvel.common.annotation.ResponseFormat;
import org.marvel.common.exception.ExceptionFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    /**
     * 获取部门树
     *
     * @return 部门树结构
     */
    @GetMapping("/departments/tree")
    @ResponseFormat
    public List<DepartmentEntity> getDepartmentTree() {
        return departmentService.getTree();
    }

    /**
     * 获取部门列表
     *
     * @return 全部部门
     */
    @GetMapping("/departments")
    @ResponseFormat
    public List<AcceptDepartment> getDepartmentList() {
        List<DepartmentEntity> entities = departmentService.getList();
        return CollectionUtil.map(entities, item -> BeanUtil.copyProperties(
                item,
                AcceptDepartment.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid,
                DepartmentEntity.Fields.children
        ), true);
    }


    @GetMapping("/department")
    @ResponseFormat
    public AcceptDepartment getDepartment(@RequestParam("id") String departmentId) {
        DepartmentEntity department = departmentService.get(departmentId);
        if (ObjectUtil.isNull(department)) {
            throw ExceptionFactory.create(1106);
        }
        return BeanUtil.copyProperties(
                department,
                AcceptDepartment.class,
                BaseEntity.Fields.createTime,
                BaseEntity.Fields.valid,
                DepartmentEntity.Fields.children
        );
    }

    /**
     * 新增部门
     *
     * @param department 部门
     * @return 部门ID
     */
    @PostMapping("/department")
    @ResponseFormat
    public String addDepartment(@RequestBody AcceptDepartment department) {
        int validateStatus = department.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (departmentService.isDuplicate(department.getCode(), null, null)) {
            // duplicate department code
            throw ExceptionFactory.create(1104);
        }
        if (departmentService.isDuplicate(null, department.getName(), null)) {
            // duplicate department name
            throw ExceptionFactory.create(1105);
        }
        if (StrUtil.isNotBlank(department.getParentId()) && !departmentService.isExistent(department.getParentId())) {
            throw ExceptionFactory.create(1107);
        }
        DepartmentEntity entity = department.convertToEntity();
        entity.setId(null);
        departmentService.save(entity);
        return entity.getId();
    }

    /**
     * 修改部门
     *
     * @param department 部门
     */
    @PutMapping("/department")
    @ResponseFormat
    public void updateDepartment(@RequestBody AcceptDepartment department) {
        int validateStatus = department.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        if (!departmentService.isExistent(department.getId())) {
            // nonexistent entity
            throw ExceptionFactory.create(1106);
        }
        if (departmentService.isDuplicate(department.getCode(), null, department.getId())) {
            // duplicate department code
            throw ExceptionFactory.create(1104);
        }
        if (departmentService.isDuplicate(null, department.getName(), department.getId())) {
            // duplicate department name
            throw ExceptionFactory.create(1105);
        }
        if (StrUtil.isNotBlank(department.getParentId()) && !departmentService.isExistent(department.getParentId())) {
            throw ExceptionFactory.create(1107);
        }
        DepartmentEntity entity = department.convertToEntity();
        departmentService.save(entity);
    }

    /**
     * 软删除部门
     *
     * @param departmentIds 部门ID列表
     */
    @PutMapping("/departments/invalidation")
    @ResponseFormat
    public int invalidateDepartments(@RequestBody List<String> departmentIds) {
        if (ObjectUtil.isNotNull(departmentIds)) {
            return departmentService.invalidate(departmentIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个部门
     *
     * @param departmentId 部门ID
     */
    @DeleteMapping("/department/{id}")
    @ResponseFormat
    public int deleteDepartment(@PathVariable("id") String departmentId) {
        if (StrUtil.isNotEmpty(departmentId)) {
            return departmentService.delete(departmentId);
        }
        return 0;
    }
}
