package org.marvel.deevy.foundation.service.security;

import org.marvel.deevy.common.service.BaseService;
import org.marvel.deevy.foundation.entity.security.User;
import org.springframework.stereotype.Service;

/**
 * @author haoyuanqiang
 * @date 2022/10/11 17:06
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Service
public interface UserService extends BaseService<User> {

    User getByUsername(String username);


    long getSessionCount(String username);


    boolean updateUserLock(String userId, Integer hasLocked);
}
