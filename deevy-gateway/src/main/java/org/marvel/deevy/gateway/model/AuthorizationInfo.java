package org.marvel.deevy.gateway.model;

import lombok.Data;

/**
 * @author haoyuanqiang
 * @date 2022/9/9 14:47
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Data
public class AuthorizationInfo {

    private String userId;


    private Long createTime;


    private Long expireTime;
}
