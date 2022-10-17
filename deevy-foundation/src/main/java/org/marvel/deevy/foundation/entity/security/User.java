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
 * @date 2022/10/11 10:36
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "MA_USER", indexes = {
        @Index(name = "MA_IN_ULI_VALID", columnList = "_VALID")
})
public class User extends BaseEntity {

    /**
     * 人员ID
     */
    @Column(name = "STAFF_ID")
    private String staffId;

    /**
     * 登录名称
     */
    @Column(name = "USERNAME")
    private String username;

    /**
     * 密码
     */
    @Column(name = "PASSWORD")
    private String password;

    /**
     * 无操作自动注销时长(分钟）
     */
    @Column(name = "AUTO_LOGOUT_DURATION")
    private Integer autoLogoutDuration;

    /**
     * 密码错误最大尝试次数
     */
    @Column(name = "MAX_RETRY_TIMES")
    private Integer maxRetryTimes;

    /**
     * 用户状态
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 是否已被锁定（密码错误次数过多）
     */
    @Column(name = "HAS_LOCKED")
    private Integer hasLocked;

    /**
     * 用户最大允许创建Session数量（最多允许并发登录数量）
     */
    @Column(name = "MAX_SESSION_COUNT")
    private Integer maxSessionCount;
}
