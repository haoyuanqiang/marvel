package org.marvel.deevy.foundation.service.organization.impl;

import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.model.PageResult;
import org.marvel.deevy.common.service.impl.BaseServiceImpl;
import org.marvel.deevy.foundation.entity.organization.Staff;
import org.marvel.deevy.foundation.repository.organization.StaffRepository;
import org.marvel.deevy.foundation.service.association.UserAndRoleAssociationService;
import org.marvel.deevy.foundation.service.organization.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public class StaffServiceImpl extends BaseServiceImpl<Staff, StaffRepository> implements StaffService {

    @Autowired
    private UserAndRoleAssociationService userAndRoleAssociationService;


    public void relateUserAndRole(String userId, String roleId) {
        userAndRoleAssociationService.save(userId, roleId);
    }


    public void relateUserAndRole(String userId, List<String> roleIds) {
        for (String roleId : roleIds) {
            relateUserAndRole(userId, roleId);
        }
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int delete(String id) {
        int result = super.delete(id);
        userAndRoleAssociationService.deleteAssociationByUserId(id);
        return result;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int delete(List<String> ids) {
        int result = super.delete(ids);
        userAndRoleAssociationService.deleteAssociationByUserId(ids);
        return result;
    }


    @Override
    public Staff getByLoginName(String loginName) {
        Specification<Staff> specification = (root, query, builder) -> {
            return builder.and(builder.equal(root.get(Staff.Fields.loginName), loginName),
                    builder.equal(root.get(BaseEntity.Fields.valid), 1));
        };
        return repository.findOne(specification).orElse(null);
    }


    @Override
    public PageResult<Staff> getList(int pageSize, int current, String departmentId, String codeOrName) {
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        Specification<Staff> specification = ((root, query, builder) -> {
            List<Predicate> conditions = new ArrayList<>(3);
            conditions.add(builder.notEqual(root.get(BaseEntity.Fields.valid), 0));
            if (StrUtil.isNotBlank(departmentId)) {
                conditions.add(builder.equal(root.get(Staff.Fields.departmentId), departmentId));
            }
            if (StrUtil.isNotBlank(codeOrName)) {
                conditions.add(builder.or(
                        builder.like(root.get(Staff.Fields.name), attachWildcard(escapeString(codeOrName))),
                        builder.like(root.get(Staff.Fields.code), attachWildcard(escapeString(codeOrName)))
                ));
            }
            query.where(conditions.toArray(new Predicate[0]));
            query.orderBy(builder.asc(root.get(Staff.Fields.sortNumber)));
            return null;
        });
        Page<Staff> page = repository.findAll(specification, pageable);
        return new PageResult<>(page.getContent(), pageSize, current, page.getTotalElements());
    }

}
