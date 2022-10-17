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
 * @date 2022/10/12 15:48
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "MA_PASSWORD_ERROR_COUNTER", indexes = {
        @Index(name = "MA_IN_PE_USER_ID", columnList = "USER_ID"),
        @Index(name = "MA_IN_PE_USERNAME", columnList = "USERNAME"),
        @Index(name = "MA_IN_PE_VALID", columnList = "_VALID")
})
public class PasswordErrorCounter extends BaseEntity {

    /**
     * 用户ID
     */
    @Column(name = "USER_ID")
    private String userId;

    /**
     * 用户名称
     */
    @Column(name = "USERNAME")
    private String username;

    /**
     * 最大允许尝试次数
     */
    @Column(name = "MAX_RETRY_TIMES")
    private Integer maxRetryTimes;

    /**
     * 剩余尝试次数
     */
    @Column(name = "REST_RETRY_TIMES")
    private Integer restRetryTimes;

}
