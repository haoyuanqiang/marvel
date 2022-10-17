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
@Table(name = "MA_USER_GROUP", indexes = {
        @Index(name = "MA_IN_UG_NAME", columnList = "NAME"),
        @Index(name = "MA_IN_UG_VALID", columnList = "_VALID")
})
public class UserGroup extends BaseEntity {

    /**
     * 编码
     */
    @Column(name = "CODE")
    private String code;

    /**
     * 名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 备注
     */
    @Column(name = "REMARKS", length = 1023)
    private String remarks;

    /**
     * 父级用户组ID
     */
    @Column(name = "PARENT_ID", length = 36)
    private String parentId;

    /**
     * 排序码
     */
    @Column(name = "SORT_NUMBER")
    private Long sortNumber;

    /**
     * 用户组状态
     */
    @Column(name = "STATUS")
    private Integer status;
}
