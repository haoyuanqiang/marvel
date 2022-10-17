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
 * @date 2022/5/2 16:34
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Entity
@Getter
@Setter
@FieldNameConstants
@Table(name = "MA_OPERATION", indexes = {
        @Index(name = "MA_IN_OP_CODE", columnList = "CODE"),
        @Index(name = "MA_IN_OP_NAME", columnList = "NAME"),
        @Index(name = "MA_IN_OP_VALID", columnList = "_VALID")
})
public class Operation extends BaseEntity {

    /**
     * 权限编码
     */
    @Column(name = "CODE")
    private String code;

    /**
     * 权限名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 菜单ID
     */
    @Column(name = "MENU_ID", length = 36)
    private String menuId;

    /**
     * 排序
     */
    @Column(name = "SORT_NUMBER")
    private Integer sortNumber;

    /**
     * 请求方法
     */
    @Column(name = "METHOD")
    private String method;

    /**
     * 权限路径
     */
    @Column(name = "ROUTE")
    private String route;

    /**
     * 不受限制的权限每个用户均可访问
     */
    @Column(name = "UNLIMITED")
    private Integer unlimited = 0;

}
