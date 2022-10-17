package org.marvel.deevy.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author haoyuanqiang
 * @date 2022/4/11 13:50
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 分页信息
     */
    private Pagination pagination;

    /**
     * 构造函数
     *
     * @param list     数据列表
     * @param pageSize 分页大小
     * @param current  当前页码
     * @param total    数据总数
     */
    public PageResult(List<T> list, int pageSize, int current, long total) {
        this.list = list;
        this.pagination = new Pagination(pageSize, current, total);
    }
}
