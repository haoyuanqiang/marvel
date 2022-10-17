package org.marvel.deevy.foundation.service.security.impl;

import cn.hutool.core.util.ObjectUtil;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.enums.EntityValidValue;
import org.marvel.deevy.common.service.impl.BaseServiceImpl;
import org.marvel.deevy.foundation.entity.security.PasswordErrorCounter;
import org.marvel.deevy.foundation.entity.security.User;
import org.marvel.deevy.foundation.repository.security.PasswordErrorCounterRepository;
import org.marvel.deevy.foundation.service.security.PasswordErrorCounterService;
import org.marvel.deevy.foundation.service.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author haoyuanqiang
 * @date 2022/10/12 15:57
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Service
public class PasswordErrorCounterServiceImpl extends BaseServiceImpl<PasswordErrorCounter, PasswordErrorCounterRepository>
        implements PasswordErrorCounterService {

    @Autowired
    private UserService userService;


    private PasswordErrorCounter getByUserId(String userId) {
        Specification<PasswordErrorCounter> specification = (root, query, builder) -> {
            return builder.and(
                    builder.equal(root.get(PasswordErrorCounter.Fields.userId), userId),
                    builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
            );
        };
        return repository.findOne(specification).orElse(null);
    }


    private PasswordErrorCounter getByUsername(String username) {
        Specification<PasswordErrorCounter> specification = (root, query, builder) -> {
            return builder.and(
                    builder.equal(root.get(PasswordErrorCounter.Fields.username), username),
                    builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
            );
        };
        return repository.findOne(specification).orElse(null);
    }


    private PasswordErrorCounter create(User user) {
        if (null == user) {
            return null;
        }
        PasswordErrorCounter counter = new PasswordErrorCounter();
        setBaseEntityProperties(counter);
        counter.setUserId(user.getId());
        counter.setUsername(user.getUsername());
        counter.setMaxRetryTimes(Math.max(0, ObjectUtil.defaultIfNull(user.getMaxRetryTimes(), 0)));
        counter.setRestRetryTimes(counter.getMaxRetryTimes());
        return counter;
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public int decrementRetryTimes(String userId) {
        PasswordErrorCounter counter = getByUserId(userId);
        if (counter == null) {
            User user = userService.getById(userId);
            counter = create(user);
        }
        counter.setRestRetryTimes(Math.max(counter.getRestRetryTimes() - 1, 0));
        save(counter);
        return counter.getRestRetryTimes();
    }


    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public void resetRetryTimes(String userId) {
        Specification<PasswordErrorCounter> specification = (root, query, builder) -> {
            return builder.equal(root.get(PasswordErrorCounter.Fields.userId), userId);
        };
        delete(specification);
    }


    @Override
    public int hasRetryTimes(String userId) {
        Specification<PasswordErrorCounter> specification = (root, query, builder) -> {
            return builder.equal(root.get(PasswordErrorCounter.Fields.userId), userId);
        };
        PasswordErrorCounter counter = getByUserId(userId);
        if (null == counter) {
            User user = userService.getById(userId);
            return user.getMaxRetryTimes();
        }
        return counter.getRestRetryTimes();
    }
}
