package org.marvel.deevy.foundation.service.security.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.common.enums.EntityValidValue;
import org.marvel.deevy.common.exception.ExceptionFactory;
import org.marvel.deevy.common.service.impl.BaseServiceImpl;
import org.marvel.deevy.foundation.entity.security.Session;
import org.marvel.deevy.foundation.entity.security.User;
import org.marvel.deevy.foundation.enums.SessionStatus;
import org.marvel.deevy.foundation.model.SessionRevokerOutput;
import org.marvel.deevy.foundation.repository.security.SessionRepository;
import org.marvel.deevy.foundation.service.organization.StaffService;
import org.marvel.deevy.foundation.service.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/9/14 17:07
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Service
@Slf4j
public class SessionServiceImpl extends BaseServiceImpl<Session, SessionRepository> implements SessionService {

    @Autowired
    private StaffService staffService;



    @Override
    public Session getByUserId(String userId) {
        Specification<Session> specification = (root, query, builder) -> {
            return builder.and(builder.equal(root.get(Session.Fields.userId), userId),
                    builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal()));
        };
        return repository.findOne(specification).orElse(null);
    }


    @Override
    public long getSessionCount(String userId) {
        Specification<Session> specification = (root, query, builder) -> {
            return builder.and(
                    builder.equal(root.get(Session.Fields.userId), userId),
                    builder.equal(root.get(Session.Fields.status), SessionStatus.SUCCESS.ordinal()),
                    builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
            );
        };
        return repository.count(specification);
    }


    @Override
    @Transactional(rollbackFor = { RuntimeException.class })
    public void expireSession(String sessionId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Session> update = builder.createCriteriaUpdate(Session.class);
        Root<Session> root = update.from(Session.class);
        long now = System.currentTimeMillis();
        update.set(root.get(Session.Fields.status), SessionStatus.FORCED_LOGOFF.ordinal());
        update.set(root.get(BaseEntity.Fields.modifyTime), now);
        update.set(root.get(Session.Fields.logoutTime), now);
        CriteriaBuilder.In<String> in = builder.in(root.get(BaseEntity.Fields.id));
        update.where(
                builder.equal(root.get(BaseEntity.Fields.id), sessionId),
                builder.equal(root.get(Session.Fields.status), SessionStatus.SUCCESS.ordinal()),
                builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
        );
        Query query = entityManager.createQuery(update);
        query.executeUpdate();
    }


    @Override
    @Transactional(rollbackFor = { RuntimeException.class })
    public void expireOriginalSession(String userId, int count) {
        if (count <= 0) {
            return;
        }
        Specification<Session> specification = (root, query, builder) -> {
            query.orderBy(builder.asc(root.get(BaseEntity.Fields.modifyTime)));
            return builder.and(
                    builder.equal(root.get(Session.Fields.userId), userId),
                    builder.equal(root.get(Session.Fields.status), SessionStatus.SUCCESS.ordinal()),
                    builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
            );
        };
        List<Session> sessions = repository.findAll(specification);
        List<String> expiredSessionId = new ArrayList<>(count);
        for (int i = 0; i < count && i < sessions.size(); ++i) {
            Session session = sessions.get(i);
            expiredSessionId.add(session.getId());
            log.debug("The session[{}] will be forced offline because the maximum number of sessions has been reached .",
                    session.getId());
        }
        if (expiredSessionId.size() > 0) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaUpdate<Session> update = criteriaBuilder.createCriteriaUpdate(Session.class);
            Root<Session> root = update.from(Session.class);
            long now = System.currentTimeMillis();
            update.set(root.get(Session.Fields.status), SessionStatus.FORCED_LOGOFF_MAX_COUNT.ordinal());
            update.set(root.get(BaseEntity.Fields.modifyTime), now);
            update.set(root.get(Session.Fields.logoutTime), now);
            CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get(BaseEntity.Fields.id));
            for (String sessionId : expiredSessionId) {
                in.value(sessionId);
            }
            update.where(in);
            Query query = entityManager.createQuery(update);
            query.executeUpdate();
        }
    }


    @Override
    public Session create(User user) {
        if (null == user) {
            throw ExceptionFactory.create("USER_NOT_FOUND");
        }
        Session session = new Session();
        setBaseEntityProperties(session);
        session.setUserId(user.getId());
        session.setUsername(user.getUsername());
        session.setStatus(SessionStatus.READY.ordinal());
        // 若不设置用户自动注销时间，默认15分钟自动注销
        session.setAutoLogoutDuration(ObjectUtil.defaultIfNull(user.getAutoLogoutDuration(), 15) * DateUnit.MINUTE.getMillis());
        long now = System.currentTimeMillis();
        session.setExpiredTime(now + session.getAutoLogoutDuration());
        session.setIssuedTime(now);
        session.setNotBeforeTime(now);
        return session;
    }


    @Override
    @Transactional(rollbackFor = { RuntimeException.class })
    public int revokeTimeoutSession(SessionRevokerOutput output) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Session> update = builder.createCriteriaUpdate(Session.class);
        Root<Session> root = update.from(Session.class);
        long now = System.currentTimeMillis();
        update.set(root.get(Session.Fields.status), SessionStatus.TIMEOUT_LOGOFF.ordinal());
        update.set(root.get(BaseEntity.Fields.modifyTime), now);
        update.set(root.get(Session.Fields.logoutTime), now);
        update.where(
                builder.equal(root.get(Session.Fields.status), SessionStatus.SUCCESS.ordinal()),
                builder.lessThan(root.get(Session.Fields.expiredTime), now),
                builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
        );
        Query query = entityManager.createQuery(update);
        int revokedCount = query.executeUpdate();
        if (null != output) {
            output.setCount(revokedCount);
            if (revokedCount > 0) {
                output.setRevokedSession(getRevokedTimeoutSessionAt(now));
            }
        }
        return revokedCount;
    }

    private List<Session> getRevokedTimeoutSessionAt(long logoutTime) {
        Specification<Session> specification = (root, query, builder) -> {
            return builder.and(
                    builder.equal(root.get(Session.Fields.status), SessionStatus.TIMEOUT_LOGOFF.ordinal()),
                    builder.equal(root.get(Session.Fields.logoutTime), logoutTime),
                    builder.equal(root.get(BaseEntity.Fields.valid), EntityValidValue.Valid.ordinal())
            );
        };
        return repository.findAll(specification);
    }
}
