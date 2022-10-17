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
@Table(name = "MA_MENU", indexes = {
        @Index(name = "MA_IN_ME_NAME", columnList = "NAME"),
        @Index(name = "MA_IN_ME_PARENT_ID", columnList = "PARENT_ID"),
        @Index(name = "MA_IN_ME_VALID", columnList = "_VALID")
})
public class Menu extends BaseEntity {

    /**
     * 菜单编码
     */
    @Column(name = "CODE")
    private String code;

    /**
     * 菜单名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 菜单图标
     */
    @Column(name = "ICON", length = 511)
    private String iconName;

    /**
     * 菜单路径
     */
    @Column(name = "PATH", length = 1023)
    private String path;

    /**
     * 菜单路径类型
     */
    @Column(name = "ROUTE_TYPE")
    private Integer routeType;

    /**
     * 类型: 1-分组  2-页面
     */
    @Column(name = "TYPE")
    private Integer type;

    /**
     * 显示状态：显示、隐藏
     */
    @Column(name = "VISIBLE")
    private Integer visible;

    /**
     * 排序
     */
    @Column(name = "SORT_NUMBER")
    private Long sortNumber;

    /**
     * 状态：0不可用1可用
     */
    @Column(name = "STATUS")
    private Integer status;


    /**
     * 父级菜单
     */
    @Column(name = "PARENT_ID", length = 36)
    private String parentId;

    // ------------------------------------------------------------------------
    // 枚举类型
    // ------------------------------------------------------------------------

    /**
     * 菜单类型枚举类
     */
    public enum MenuType {
        /**
         * 默认
         */
        DEFAULT,
        /**
         * 目录
         */
        DIRECTORY,
        /**
         * 页面
         */
        PAGE;
    }


    public enum MenuVisible {
        /**
         * 默认
         */
        DEFAULT,
        /**
         * 显示
         */
        SHOW,
        /**
         * 隐藏
         */
        HIDE;
    }


    public enum MenuStatus {
        /**
         * 默认
         */
        DEFAULT,
        /**
         * 启用
         */
        ENABLE,
        /**
         * 停用
         */
        DISABLE;
    }

    public enum RouteType {
        /**
         * 默认
         */
        DEFAULT,
        /**
         * 系统
         */
        SYSTEM,
        /**
         * 模块
         */
        MODULE,
        /**
         * 外部
         */
        EXTERNAL;
    }

}
