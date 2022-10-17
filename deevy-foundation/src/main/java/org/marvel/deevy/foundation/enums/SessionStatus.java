package org.marvel.deevy.foundation.enums;

/**
 * @author haoyuanqiang
 * @date 2022/10/11 15:33
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
public enum SessionStatus {
    /**
     * 登录成功（保持在线状态）
     */
    SUCCESS,
    /**
     * 就绪（但不可用）
     */
    READY,
    /**
     * 正常注销
     */
    LOGOUT,
    /**
     * 超时注销
     */
    TIMEOUT_LOGOFF,
    /**
     * 强制注销(手动注销登录)
     */
    FORCED_LOGOFF,
    /**
     * 强制注销(最大数量)
     */
    FORCED_LOGOFF_MAX_COUNT,
    /**
     * 密码错误（当达到限制次数时，锁定用户）
     */
    PASSWORD_ERROR;
}
