package org.marvel.deevy.foundation.service.security;

import org.springframework.stereotype.Service;

/**
 * @author haoyuanqiang
 * @date 2022/10/12 15:56
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Service
public interface PasswordErrorCounterService {

    /**
     * 减少一次登录重试次数
     *
     * @param userId 用户ID
     * @return 剩余重试次数
     */
    int decrementRetryTimes(String userId);

    /**
     * 重置登录重试次数
     *
     * @param userId 用户ID
     */
    void resetRetryTimes(String userId);

    /**
     * 获取指定用户剩余重试次数
     *
     * @param userId 用户ID
     * @return 剩余重试次数
     */
    int hasRetryTimes(String userId);
}
