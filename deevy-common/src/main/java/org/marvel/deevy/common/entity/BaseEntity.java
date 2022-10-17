package org.marvel.deevy.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.marvel.deevy.common.enums.EntityValidValue;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author haoyuanqiang
 * @date 2022/4/8 14:02
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Getter
@Setter
@FieldNameConstants
@MappedSuperclass
public class BaseEntity {

    // ------------------------------------- //
    // 常用数据库 Varchar 字段长度定义
    // ------------------------------------- //
    protected final static int PRIMARY_KEY_LENGTH = 36;

    protected final static int COMMON_KEY_LENGTH = 64;

    protected final static int NAME_LENGTH = 255;

    protected final static int CODE_LENGTH = 127;

    protected final static int COMMENT_LENGTH = 1023;

    protected final static int JSON_LENGTH = 8000;

    /**
     * 主键, ID
     */
    @Id
    @Column(name = "_ID", length = PRIMARY_KEY_LENGTH)
    private String id;

    /**
     * 数据创建时间
     */
    @JsonIgnore
    @Column(name = "_CREATE_TIME")
    private Long createTime;

    /**
     * 数据修改时间
     */
    @Column(name = "_MODIFY_TIME")
    private Long modifyTime;

    /**
     * 数据有效性，0 表示无效数据，大于 0 表示有效
     * 取值参见{@link EntityValidValue}
     */
    @JsonIgnore
    @Column(name = "_VALID")
    private Integer valid;
}
