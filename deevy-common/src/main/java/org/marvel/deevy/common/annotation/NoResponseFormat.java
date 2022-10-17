package org.marvel.deevy.common.annotation;

import java.lang.annotation.*;

/**
 * Http响应包装标识注解
 * 使用此注解的Controller接口的返回值将不进行包装
 *
 * @author haoyuanqiang
 * @date 2022/4/8 14:02
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoResponseFormat {
}
