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

package org.marvel.admin.service.impl;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.marvel.admin.entity.BaseEntity;
import org.marvel.admin.entity.DepartmentEntity;
import org.marvel.admin.repository.DepartmentRepository;
import org.marvel.admin.service.DepartmentService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DepartmentServiceImpl extends BaseServiceImpl<DepartmentEntity, DepartmentRepository>
        implements DepartmentService {

    @Override
    public Boolean isDuplicate(String code, String name, String ignorantId) {
        Map<String, Object> matches = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(code)) {
            matches.put(DepartmentEntity.Fields.code, code);
        }
        if (StrUtil.isNotEmpty(name)) {
            matches.put(DepartmentEntity.Fields.name, name);
        }
        Map<String, Object> ignorance = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(ignorantId)) {
            ignorance.put(BaseEntity.Fields.id, ignorantId);
        }
        return super.isDuplicate(matches, ignorance);
    }

    @Override
    public List<DepartmentEntity> getList() {
        Specification<DepartmentEntity> spec = (root, query, builder) -> {
            query.where(builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
            query.orderBy(builder.asc(root.get(DepartmentEntity.Fields.sortNumber)));
            return null;
        };
        return repository.findAll(spec);
    }

    @Override
    public List<DepartmentEntity> getTree() {
        List<DepartmentEntity> departments = getList();
        Map<Object, DepartmentEntity> tmpMap = new LinkedHashMap<>(departments.size());
        List<DepartmentEntity> result = new ArrayList<>();
        for (DepartmentEntity department : departments) {
            tmpMap.put(department.getId(), department);
        }
        for (DepartmentEntity department : departments) {
            DepartmentEntity node = tmpMap.get(department.getParentId());
            if (ObjectUtil.isNotNull(node) && CompareUtil.compare(department.getId(), department.getParentId()) != 0) {
                node.addChild(tmpMap.get(department.getId()));
            } else {
                result.add(tmpMap.get(department.getId()));
            }
        }
        return result;
    }
}
