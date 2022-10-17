package org.marvel.deevy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author haoyuanqiang
 * @date 2022/4/12 18:56
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class InterfaceException extends RuntimeException {
    /**
     * 异常标识码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 自定义数据
     */
    private Object result;
}
