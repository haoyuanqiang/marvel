package org.marvel.deevy.foundation.param.framework;

import lombok.Data;

/**
 * @author haoyuanqiang
 * @date 2022/9/13 17:13
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Data
public class UserLoginParam {

    private String username;


    private String password;


    private Boolean rememberMe;
}
