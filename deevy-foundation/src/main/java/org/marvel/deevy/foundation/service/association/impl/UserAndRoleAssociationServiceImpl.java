package org.marvel.deevy.foundation.service.association.impl;

import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.service.impl.JPAQueryServiceImpl;
import org.marvel.deevy.foundation.entity.association.QUserAndRoleAssociation;
import org.marvel.deevy.foundation.entity.association.UserAndRoleAssociation;
import org.marvel.deevy.foundation.entity.organization.QStaff;
import org.marvel.deevy.foundation.entity.organization.Staff;
import org.marvel.deevy.foundation.entity.security.QRole;
import org.marvel.deevy.foundation.entity.security.Role;
import org.marvel.deevy.foundation.repository.association.UserAndRoleAssociationRepository;
import org.marvel.deevy.foundation.service.association.UserAndRoleAssociationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.SQLException;
import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Service
public class UserAndRoleAssociationServiceImpl
        extends JPAQueryServiceImpl<UserAndRoleAssociation, UserAndRoleAssociationRepository>
        implements UserAndRoleAssociationService {

    @Override
    public void save(String userId, String roleId) {
        List<UserAndRoleAssociation> associations = getAll((root, query, builder) -> {
            return builder.and(builder.equal(root.get(UserAndRoleAssociation.Fields.userId), userId),
                    builder.equal(root.get(UserAndRoleAssociation.Fields.roleId), roleId));
        });
        if (null == associations || associations.size() == 0) {
            UserAndRoleAssociation association = new UserAndRoleAssociation();
            association.setRoleId(roleId);
            association.setUserId(userId);
            super.save(association);
        }
    }

    @Override
    public int delete(String userId, String roleId) {
        return super.delete((root, query, builder) -> {
            return builder.and(builder.equal(root.get(UserAndRoleAssociation.Fields.userId), userId),
                    builder.equal(root.get(UserAndRoleAssociation.Fields.roleId), roleId));
        });
    }

    @Override
    public List<Role> getRolesByUserId(String userId) {
        QRole ROLE = QRole.role;
        QUserAndRoleAssociation USER_ROLE_ASSOCIATION = QUserAndRoleAssociation.userAndRoleAssociation;
        return jpaQueryFactory.selectFrom(ROLE)
                .innerJoin(USER_ROLE_ASSOCIATION)
                .on(ROLE.id.eq(USER_ROLE_ASSOCIATION.roleId))
                .where(USER_ROLE_ASSOCIATION.userId.eq(userId), ROLE.valid.gt(0))
                .fetch();
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int deleteAssociationByUserId(String userId) {
        return super.delete((root, query, builder) -> {
            return builder.and(builder.equal(root.get(UserAndRoleAssociation.Fields.userId), userId),
                    builder.greaterThan(root.get(BaseEntity.Fields.valid), 0));
        });
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int deleteAssociationByUserId(List<String> userIds) {
        return super.delete((root, query, builder) -> {
            CriteriaBuilder.In<String> in = builder.in(root.get(UserAndRoleAssociation.Fields.userId));
            for (String userId : userIds) {
                in.value(userId);
            }
            return builder.and(in, builder.greaterThan(root.get(BaseEntity.Fields.valid), 0));
        });
    }


    @Override
    public List<Staff> getUsersByRoleId(String roleId) {
        QStaff STAFF = QStaff.staff;
        QUserAndRoleAssociation USER_ROLE_ASSOCIATION = QUserAndRoleAssociation.userAndRoleAssociation;
        return jpaQueryFactory.selectFrom(STAFF)
                .innerJoin(USER_ROLE_ASSOCIATION)
                .on(STAFF.id.eq(USER_ROLE_ASSOCIATION.userId))
                .where(USER_ROLE_ASSOCIATION.roleId.eq(roleId), STAFF.valid.gt(0))
                .fetch();
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int deleteAssociationByRoleId(String roleId) {
        return super.delete((root, query, builder) -> {
            return builder.and(builder.equal(root.get(UserAndRoleAssociation.Fields.roleId), roleId),
                    builder.greaterThan(root.get(BaseEntity.Fields.valid), 0));
        });
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int deleteAssociationByRoleId(List<String> roleIds) {
        return super.delete((root, query, builder) -> {
            CriteriaBuilder.In<String> in = builder.in(root.get(UserAndRoleAssociation.Fields.roleId));
            for (String roleId : roleIds) {
                in.value(roleId);
            }
            return builder.and(in, builder.greaterThan(root.get(BaseEntity.Fields.valid), 0));
        });
    }

}
