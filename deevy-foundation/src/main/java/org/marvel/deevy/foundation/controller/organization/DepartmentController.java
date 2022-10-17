package org.marvel.deevy.foundation.controller.organization;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.foundation.entity.organization.Department;
import org.marvel.deevy.foundation.param.DepartmentParam;
import org.marvel.deevy.foundation.service.organization.DepartmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haoyuanqiang
 * @date 2022/1/1 19:34
 * @Copyright © 2016-2022 MARVEL
 */
@RestController
@RequestMapping("/protected/department")
public class DepartmentController {
    @Resource
    private DepartmentService departmentService;


    /**
     * 获取部门树
     *
     * @return 部门树结构
     */
    @GetMapping("/tree")
    public List<DepartmentParam> getDepartmentTree() {
        List<Department> departments = departmentService.getAll();

        Map<Object, DepartmentParam> tmpMap = new LinkedHashMap<>(departments.size());

        for (Department department : departments) {
            DepartmentParam param = BeanUtil.copyProperties(department, DepartmentParam.class);
            tmpMap.put(department.getId(), param);
        }
        List<DepartmentParam> result = new ArrayList<>();
        for (Department department : departments) {
            DepartmentParam node = tmpMap.get(department.getParentId());
            if (ObjectUtil.isNotNull(node) && CompareUtil.compare(department.getId(), department.getParentId()) != 0) {
                node.addChildNode(tmpMap.get(department.getId()));
            } else {
                result.add(tmpMap.get(department.getId()));
            }
        }
        return result;
    }

    /**
     * 获取部门列表
     *
     * @return 全部部门
     */
    @GetMapping("/list")
    public List<DepartmentParam> getDepartmentList() {
        List<Department> entities = departmentService.getAll();
        return CollectionUtil.map(entities, item -> BeanUtil.copyProperties(item, DepartmentParam.class), true);
    }


    @GetMapping
    public DepartmentParam getDepartment(@RequestParam("id") String departmentId) {
        Department department = departmentService.getById(departmentId);
        if (ObjectUtil.isNull(department)) {
            throw ExceptionFactory.create(1106);
        }
        return BeanUtil.copyProperties(department, DepartmentParam.class);
    }

    /**
     * 新增部门
     *
     * @param department 部门
     * @return 部门ID
     */
    @PostMapping
    public String addDepartment(@RequestBody DepartmentParam department) {
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
        Department entity = BeanUtil.copyProperties(department, Department.class, BaseEntity.Fields.createTime);
        entity.setId(null);
        departmentService.save(entity);
        return entity.getId();
    }

    /**
     * 修改部门
     *
     * @param department 部门
     */
    @PutMapping
    public void updateDepartment(@RequestBody DepartmentParam department) {
        int validateStatus = department.validate();
        if (validateStatus > 0) {
            throw ExceptionFactory.create(validateStatus);
        }
        Department entity = departmentService.getById(department.getId());
        if (null == entity) {
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
        BeanUtil.copyProperties(department, entity, CopyOptions.create().ignoreNullValue().setIgnoreProperties(
                BaseEntity.Fields.createTime, BaseEntity.Fields.modifyTime));
        departmentService.save(entity);
    }

    /**
     * 软删除部门
     *
     * @param departmentIds 部门ID列表
     */
    @PutMapping("/invalidation")
    public int invalidateDepartments(@RequestBody List<String> departmentIds) {
        if (ObjectUtil.isNotNull(departmentIds)) {
            return departmentService.invalidate(departmentIds);
        }
        return 0;
    }

    /**
     * 彻底删除一个部门
     *
     * @param departmentIds 部门ID
     */
    @DeleteMapping("/{ids}")
    public int deleteDepartment(@PathVariable("ids") List<String> departmentIds) {
        if (CollectionUtil.isNotEmpty(departmentIds)) {
            return departmentService.delete(departmentIds);
        }
        return 0;
    }

}
