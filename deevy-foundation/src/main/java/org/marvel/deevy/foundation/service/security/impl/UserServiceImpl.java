package org.marvel.deevy.foundation.service.security.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.enums.EntityValidValue;
import org.marvel.deevy.common.service.impl.BaseServiceImpl;
import org.marvel.deevy.foundation.entity.security.User;
import org.marvel.deevy.foundation.enums.EntityStatus;
import org.marvel.deevy.foundation.repository.security.UserRepository;
import org.marvel.deevy.foundation.service.security.UserService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;


/**
 * @author haoyuanqiang
 * @date 2022/10/11 17:06
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User, UserRepository> implements UserService {

    @Override
    public User getByUsername(String username) {
        Specification<User> specification = (root, query, builder) -> {
            return builder.and(
                    builder.equal(root.get(User.Fields.username), username),
                    builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
            );
        };
        return CollectionUtil.getFirst(getAll(specification));
    }


    @Override
    public long getSessionCount(String username) {
        Specification<User> specification = (root, query, builder) -> {
            return builder.and(
                    builder.equal(root.get(User.Fields.username), username),
                    builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
            );
        };
        return repository.count(specification);
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public boolean updateUserLock(String userId, Integer hasLocked) {
        if (StrUtil.isBlank(userId)) {
            return false;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<User> update = criteriaBuilder.createCriteriaUpdate(User.class);
        Root<User> root = update.from(User.class);
        update.set(root.get(User.Fields.hasLocked), hasLocked);
        update.where(
                criteriaBuilder.equal(root.get(BaseEntity.Fields.id), userId),
                criteriaBuilder.notEqual(root.get(BaseEntity.Fields.valid), EntityValidValue.Invalid.ordinal()),
                criteriaBuilder.notEqual(root.get(User.Fields.status), EntityStatus.DISABLED.ordinal())
        );
        Query query = entityManager.createQuery(update);
        return query.executeUpdate() > 0;
    }
}
