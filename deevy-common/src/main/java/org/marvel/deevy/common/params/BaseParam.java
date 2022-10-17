package org.marvel.deevy.common.params;

import lombok.Getter;
import lombok.Setter;

/**
 * @author haoyuanqiang
 * @date 2022/4/13 13:05
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Getter
@Setter
public class BaseParam {

    /**
     * ID
     */
    protected String id;

    /**
     * 数据创建时间
     */
    protected Long createTime;

    /**
     * 数据修改时间
     */
    protected Long modifyTime;
}
