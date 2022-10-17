package org.marvel.deevy.foundation.entity.security;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.marvel.deevy.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author haoyuanqiang
 * @date 2022/9/10 17:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "MA_SESSION", indexes = {
        @Index(name = "MA_IN_OU_USER_ID", columnList = "USER_ID"),
        @Index(name = "MA_IN_OU_USERNAME", columnList = "USERNAME"),
        @Index(name = "MA_IN_OU_EXPIRED_TIME", columnList = "EXPIRED_TIME"),
        @Index(name = "MA_IN_OU_LOGOUT_TIME", columnList = "LOGOUT_TIME"),
        @Index(name = "MA_IN_OU_VALID", columnList = "_VALID")
})
public class Session extends BaseEntity {

    /**
     * 签发时间(登录时间）
     */
    @Column(name = "ISSUED_TIME")
    private Long issuedTime;

    /**
     * Token 的过期时间，这个过期时间必须要大于签发时间
     */
    @Column(name = "EXPIRED_TIME")
    private Long expiredTime;

    /**
     * 生效时间，定义在什么时间之前，该 Token 都是不可用的.
     */
    @Column(name = "NOT_BEFORE_TIME")
    private Long notBeforeTime;

    /**
     * 自动注销时长
     */
    @Column(name = "AUTO_LOGOUT_DURATION")
    private Long autoLogoutDuration;

    /**
     * 登录用户ID
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 登录用户名称
     */
    @Column(name = "USERNAME")
    private String username;

    /**
     * 登录IP
     */
    @Column(name = "IP")
    private String ip;

    /**
     * 登录浏览器
     */
    @Column(name = "BROWSER")
    private String browser;

    /**
     * 操作系统
     */
    @Column(name = "OPERATION_SYSTEM")
    private String operationSystem;

    /**
     * 会话状态
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 会话注销时间（登出时间）
     */
    @Column(name = "LOGOUT_TIME")
    private Long logoutTime;
}
