package org.marvel.deevy.gateway.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author haoyuanqiang
 * @date 2022/9/9 14:12
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Data
@NoArgsConstructor
public class HttpResult {

    private Integer code;


    private Integer subCode;

    private String message;


    public HttpResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
