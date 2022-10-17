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
@Table(name = "MA_ROLE", indexes = {
        @Index(name = "MA_IN_RO_NAME", columnList = "NAME"),
        @Index(name = "MA_IN_CF_VALID", columnList = "_VALID")
})
public class Role extends BaseEntity {

    /**
     * 角色编码
     */
    @Column(name = "CODE", length = 255)
    private String code;

    /**
     * 角色名称
     */
    @Column(name = "NAME", length = 255)
    private String name;

    /**
     * 备注
     */
    @Column(name = "REMARKS", length = 511)
    private String remarks;

    /**
     * 状态：正常，停用
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 排序码
     */
    @Column(name = "SORT_NUMBER")
    private Long sortNumber;

}
