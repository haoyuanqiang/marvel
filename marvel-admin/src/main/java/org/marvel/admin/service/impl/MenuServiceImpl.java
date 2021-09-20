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
import org.marvel.admin.entity.MenuEntity;
import org.marvel.admin.repository.MenuRepository;
import org.marvel.admin.service.MenuService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl extends BaseServiceImpl<MenuEntity, MenuRepository> implements MenuService {
    @Override
    public Boolean isDuplicate(String code, String name, String ignorantId) {
        Map<String, Object> matches = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(code)) {
            matches.put(MenuEntity.Fields.code, code);
        }
        if (StrUtil.isNotEmpty(name)) {
            matches.put(MenuEntity.Fields.name, name);
        }
        Map<String, Object> ignorance = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(ignorantId)) {
            ignorance.put(BaseEntity.Fields.id, ignorantId);
        }
        return super.isDuplicate(matches, ignorance);
    }

    @Override
    public List<MenuEntity> getList() {
        Specification<MenuEntity> spec = (root, query, builder) -> {
            query.where(builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
            query.orderBy(builder.asc(root.get(MenuEntity.Fields.sortNumber)));
            return null;
        };
        return repository.findAll(spec);
    }

    @Override
    public List<MenuEntity> getTree() {
        List<MenuEntity> menus = getList();
        Map<Object, MenuEntity> tmpMap = new LinkedHashMap<>(menus.size());
        List<MenuEntity> result = new ArrayList<>();
        for (MenuEntity menu : menus) {
            tmpMap.put(menu.getId(), menu);
        }
        for (MenuEntity menu : menus) {
            MenuEntity node = tmpMap.get(menu.getParentId());
            if (ObjectUtil.isNotNull(node) && CompareUtil.compare(menu.getId(), menu.getParentId()) != 0) {
                node.appendChild(tmpMap.get(menu.getId()));
            } else {
                result.add(tmpMap.get(menu.getId()));
            }
        }
        return result;
    }
}
