package org.marvel.deevy.foundation.service.security.impl;

import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.model.PageResult;
import org.marvel.deevy.common.service.impl.BaseServiceImpl;
import org.marvel.deevy.foundation.entity.security.Role;
import org.marvel.deevy.foundation.repository.security.RoleRepository;
import org.marvel.deevy.foundation.service.security.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<Role, RoleRepository> implements RoleService {

    @Override
    public boolean isDuplicate(String code, String name, String ignorantId) {
        Map<String, Object> matches = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(code)) {
            matches.put(Role.Fields.code, code);
        }
        if (StrUtil.isNotEmpty(name)) {
            matches.put(Role.Fields.name, name);
        }
        Map<String, Object> ignorance = new LinkedHashMap<>();
        if (StrUtil.isNotEmpty(ignorantId)) {
            ignorance.put(BaseEntity.Fields.id, ignorantId);
        }
        return super.isDuplicate(matches, ignorance);
    }

    @Override
    public List<Role> getAll() {
        Specification<Role> specification = (root, query, builder) -> {
            query.orderBy(builder.asc(root.get(Role.Fields.sortNumber)));
            return builder.notEqual(root.get(BaseEntity.Fields.valid), 0);
        };
        return repository.findAll(specification);
    }

    @Override
    public PageResult<Role> getList(int pageSize, int current, String code, String name) {
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        Specification<Role> spec = (root, query, builder) -> {
            List<Predicate> conditions = new ArrayList<>(3);
            conditions.add(builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
            if (StrUtil.isNotBlank(code)) {
                conditions.add(builder.like(root.get(Role.Fields.code),
                        StrUtil.format("%{}%", escapeString(code))));
            }
            if (StrUtil.isNotBlank(name)) {
                conditions.add(builder.like(root.get(Role.Fields.name),
                        StrUtil.format("%{}%", escapeString(name))));
            }
            query.where(conditions.toArray(new Predicate[0]));
            query.orderBy(builder.asc(root.get(Role.Fields.sortNumber)));
            return null;
        };
        Page<Role> list = repository.findAll(spec, pageable);
        return new PageResult<>(list.getContent(), pageSize, current, list.getTotalElements());
    }
}
