package org.marvel.deevy.foundation.service.organization.impl;

import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.service.impl.BaseServiceImpl;
import org.marvel.deevy.foundation.entity.organization.Department;
import org.marvel.deevy.foundation.repository.organization.DepartmentRepository;
import org.marvel.deevy.foundation.service.organization.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<Department, DepartmentRepository> implements DepartmentService {

    @Override
    public Boolean isDuplicate(String code, String name, String ignorantId) {
        Map<String, Object> matches = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(code)) {
            matches.put(Department.Fields.code, code);
        }
        if (StrUtil.isNotEmpty(name)) {
            matches.put(Department.Fields.name, name);
        }
        Map<String, Object> ignorance = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(ignorantId)) {
            ignorance.put(BaseEntity.Fields.id, ignorantId);
        }
        return super.isDuplicate(matches, ignorance);
    }
}
