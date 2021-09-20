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

import cn.hutool.core.util.StrUtil;
import org.marvel.admin.entity.BaseEntity;
import org.marvel.admin.entity.PermissionEntity;
import org.marvel.admin.entity.QPermissionEntity;
import org.marvel.admin.repository.PermissionRepository;
import org.marvel.admin.service.PermissionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionServiceImpl extends BaseServiceImpl<PermissionEntity, PermissionRepository>
        implements PermissionService {
    @Override
    public List<PermissionEntity> getList() {
        Specification<PermissionEntity> spec = (root, query, builder) -> {
            query.where(builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
            query.orderBy(builder.asc(root.get(PermissionEntity.Fields.sortNumber)));
            return null;
        };
        return repository.findAll(spec);
    }

    @Override
    public List<PermissionEntity> getListByMenuId(String menuId) {
        Specification<PermissionEntity> spec = (root, query, builder) -> {
            List<Predicate> conditions = new ArrayList<>(2);
            conditions.add(builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
            if (StrUtil.isNotBlank(menuId)) {
                conditions.add(builder.equal(root.get(PermissionEntity.Fields.menuId), menuId));
            }
            query.where(conditions.toArray(new Predicate[0]));
            query.orderBy(builder.asc(root.get(PermissionEntity.Fields.sortNumber)));
            return null;
        };
        return repository.findAll(spec);
    }

    @Override
    public List<PermissionEntity> getUnlimitedPermissions() {
        QPermissionEntity permissionEntity = QPermissionEntity.permissionEntity;
        return jpaQueryFactory.select(permissionEntity)
                .from(permissionEntity)
                .where(permissionEntity.unlimited.gt(0))
                .fetch();
    }
}
