package org.marvel.deevy.foundation.entity.organization;

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
@Table(name = "MA_STAFF", indexes = {
        @Index(name = "MA_IN_RO_NAME", columnList = "NAME"),
        @Index(name = "MA_IN_CF_VALID", columnList = "_VALID")
})
public class Staff extends BaseEntity {

    /**
     * 人员编码
     */
    @Column(name = "CODE")
    private String code;

    /**
     * 人员名称
     */
    @Column(name = "NAME")
    private String name;

    /**
     * 人员昵称
     */
    @Column(name = "NICKNAME")
    private String nickname;

    /**
     * 登录名称
     */
    @Column(name = "LOGIN_NAME")
    private String loginName;

    /**
     * 电话号码
     */
    @Column(name = "TELEPHONE", length = 31)
    private String telephone;

    /**
     * 邮箱地址
     */
    @Column(name = "EMAIL")
    private String email;

    /**
     * 性别： 1 = 男，2 = 女，3 = 保密
     */
    @Column(name = "SEX")
    private Integer sex;

    /**
     * 头像，资源ID，图像数据存于Resource表中
     */
    @Column(name = "PORTRAIT_ID")
    private String portraitId;

    /**
     * 人员状态
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 部门ID
     */
    @Column(name = "DEPARTMENT_ID", length = 63)
    private String departmentId;

    /**
     * 备注
     */
    @Column(name = "REMARKS", length = 511)
    private String remarks;

    /**
     * 排序码
     */
    @Column(name = "SORT_NUMBER")
    private Long sortNumber;

    /**
     * 是否只读，用于标识不可删除的数据
     */
    @Column(name = "READ_ONLY")
    private Boolean readOnly;

}
