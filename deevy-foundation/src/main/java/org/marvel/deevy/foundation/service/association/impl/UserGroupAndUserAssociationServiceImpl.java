package org.marvel.deevy.foundation.service.association.impl;

import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.service.impl.JPAQueryServiceImpl;
import org.marvel.deevy.foundation.entity.association.QUserGroupAndUserAssociation;
import org.marvel.deevy.foundation.entity.association.UserGroupAndUserAssociation;
import org.marvel.deevy.foundation.entity.organization.QStaff;
import org.marvel.deevy.foundation.entity.organization.Staff;
import org.marvel.deevy.foundation.entity.security.QUserGroup;
import org.marvel.deevy.foundation.entity.security.UserGroup;
import org.marvel.deevy.foundation.repository.association.UserGroupAndUserAssociationRepository;
import org.marvel.deevy.foundation.service.association.UserGroupAndUserAssociationService;
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
public class UserGroupAndUserAssociationServiceImpl extends JPAQueryServiceImpl<UserGroupAndUserAssociation, UserGroupAndUserAssociationRepository>
        implements UserGroupAndUserAssociationService {

    @Override
    public void save(String userGroupId, String userId) {
        List<UserGroupAndUserAssociation> associations = getAll((root, query, builder) -> {
            return builder.and(builder.equal(root.get(UserGroupAndUserAssociation.Fields.userId), userId),
                    builder.equal(root.get(UserGroupAndUserAssociation.Fields.userGroupId), userGroupId));
        });
        if (null == associations || associations.size() == 0) {
            UserGroupAndUserAssociation association = new UserGroupAndUserAssociation();
            association.setUserGroupId(userGroupId);
            association.setUserId(userId);
            super.save(association);
        }
    }

    @Override
    public int delete(String userGroupId, String userId) {
        return super.delete((root, query, builder) -> {
            return builder.and(builder.equal(root.get(UserGroupAndUserAssociation.Fields.userId), userId),
                    builder.equal(root.get(UserGroupAndUserAssociation.Fields.userGroupId), userGroupId));
        });
    }

    @Override
    public List<UserGroup> getUserGroupsByUserId(String userId) {
        QUserGroup USER_GROUP = QUserGroup.userGroup;
        QUserGroupAndUserAssociation USER_GROUP_USER_ASSOCIATION = QUserGroupAndUserAssociation.userGroupAndUserAssociation;
        return jpaQueryFactory.selectFrom(USER_GROUP)
                .innerJoin(USER_GROUP_USER_ASSOCIATION)
                .on(USER_GROUP.id.eq(USER_GROUP_USER_ASSOCIATION.userGroupId))
                .where(USER_GROUP_USER_ASSOCIATION.userId.eq(userId), USER_GROUP.valid.gt(0))
                .fetch();
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int deleteAssociationByUserId(String userId) {
        return super.delete((root, query, builder) -> {
            return builder.and(builder.equal(root.get(UserGroupAndUserAssociation.Fields.userId), userId),
                    builder.greaterThan(root.get(BaseEntity.Fields.valid), 0));
        });
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int deleteAssociationByUserId(List<String> userIds) {
        return super.delete((root, query, builder) -> {
            CriteriaBuilder.In<String> in = builder.in(root.get(UserGroupAndUserAssociation.Fields.userId));
            for (String userId : userIds) {
                in.value(userId);
            }
            return builder.and(in, builder.greaterThan(root.get(BaseEntity.Fields.valid), 0));
        });
    }

    @Override
    public List<Staff> getUsersByUserGroupId(String userGroupId) {
        QStaff STAFF = QStaff.staff;
        QUserGroupAndUserAssociation USER_GROUP_USER_ASSOCIATION = QUserGroupAndUserAssociation.userGroupAndUserAssociation;
        return jpaQueryFactory.selectFrom(STAFF)
                .innerJoin(USER_GROUP_USER_ASSOCIATION)
                .on(STAFF.id.eq(USER_GROUP_USER_ASSOCIATION.userId))
                .where(USER_GROUP_USER_ASSOCIATION.userGroupId.eq(userGroupId), STAFF.valid.gt(0))
                .fetch();
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int deleteAssociationByUserGroupId(String userGroupId) {
        return super.delete((root, query, builder) -> {
            return builder.and(builder.equal(root.get(UserGroupAndUserAssociation.Fields.userGroupId), userGroupId),
                    builder.greaterThan(root.get(BaseEntity.Fields.valid), 0));
        });
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, SQLException.class})
    public int deleteAssociationByUserGroupId(List<String> userGroupIds) {
        return super.delete((root, query, builder) -> {
            CriteriaBuilder.In<String> in = builder.in(root.get(UserGroupAndUserAssociation.Fields.userGroupId));
            for (String userGroupId : userGroupIds) {
                in.value(userGroupId);
            }
            return builder.and(in, builder.greaterThan(root.get(BaseEntity.Fields.valid), 0));
        });
    }
}
