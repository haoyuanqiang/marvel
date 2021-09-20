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
import org.marvel.admin.entity.BaseEntity;
import org.marvel.admin.entity.PositionEntity;
import org.marvel.admin.repository.PositionRepository;
import org.marvel.admin.service.PositionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PositionServiceImpl extends BaseServiceImpl<PositionEntity, PositionRepository> implements PositionService {
    @Override
    public Boolean isDuplicate(String code, String name, String ignorantId) {
        Map<String, Object> matches = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(code)) {
            matches.put(PositionEntity.Fields.code, code);
        }
        if (StrUtil.isNotEmpty(name)) {
            matches.put(PositionEntity.Fields.name, name);
        }
        Map<String, Object> ignorance = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(ignorantId)) {
            ignorance.put("id", ignorantId);
        }
        return super.isDuplicate(matches, ignorance);
    }


    @Override
    public List<PositionEntity> getList() {
        Specification<PositionEntity> spec = (root, query, builder) -> {
            query.where(builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
            query.orderBy(builder.asc(root.get(PositionEntity.Fields.sortNumber)));
            return null;
        };
        return repository.findAll(spec);
    }

    @Override
    public List<PositionEntity> getTree() {
        List<PositionEntity> positions = getList();
        Map<Object, PositionEntity> tmpMap = new LinkedHashMap<>(positions.size());
        List<PositionEntity> result = new ArrayList<>();
        for (PositionEntity position : positions) {
            tmpMap.put(position.getId(), position);
        }
        for (PositionEntity position : positions) {
            PositionEntity node = tmpMap.get(position.getParentId());
            if (ObjectUtil.isNotNull(node) && CompareUtil.compare(position.getId(), position.getParentId()) != 0) {
                node.addChild(tmpMap.get(position.getId()));
            } else {
                result.add(tmpMap.get(position.getId()));
            }
        }
        return result;
    }
}
