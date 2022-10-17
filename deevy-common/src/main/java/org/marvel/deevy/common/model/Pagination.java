package org.marvel.deevy.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author haoyuanqiang
 * @date 2022/4/11 13:49
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

    /**
     * 分页大小
     */
    private int pageSize;

    /**
     * 当前页码
     */
    private int current;

    /**
     * 数据总数
     */
    private long total;

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public long getTotalPages() {
        return (total + pageSize - 1) / Math.max(pageSize, 1);
    }
}
