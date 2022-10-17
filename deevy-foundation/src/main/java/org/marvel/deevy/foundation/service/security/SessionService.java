package org.marvel.deevy.foundation.service.security;

import org.marvel.deevy.foundation.entity.security.Session;
import org.marvel.deevy.foundation.entity.security.User;
import org.marvel.deevy.foundation.model.SessionRevokerOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author haoyuanqiang
 * @date 2022/9/14 17:05
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Service
public interface SessionService {

    /**
     * 保存实体信息
     *
     * @param entity 实体信息
     * @return 实体信息
     */
    Session save(Session entity);

    /**
     * 根据 id 获取实体信息
     *
     * @param id 实体 id
     * @return 实体信息
     */
    Session getById(String id);


    Session getByUserId(String userId);


    long getSessionCount(String userId);


    void expireSession(String sessionId);


    void expireOriginalSession(String userId, int count);


    Session create(User user);

    int revokeTimeoutSession(SessionRevokerOutput output);
}
