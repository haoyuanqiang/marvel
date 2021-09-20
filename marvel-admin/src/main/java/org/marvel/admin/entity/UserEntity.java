/*
 * Licensed to the organization of MARVEL under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The MARVEL licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.marvel.admin.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author haoyuanqiang
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Table(name = "MV_USER", indexes = {
        @Index(name = "MV_IN_U_NAME", columnList = "NAME"),
        @Index(name = "MV_IN_U_VALID", columnList = "VALID")
})
public class UserEntity extends BaseEntity {
    /**
     * 用户编码
     */
    @Column(name = "CODE", length = 127)
    private String code;

    /**
     * 用户名称
     */
    @Column(name = "NAME", length = 127)
    private String name;

    /**
     * 用户昵称
     */
    @Column(name = "NICKNAME", length = 127)
    private String nickname;

    /**
     * 登录名称
     */
    @Column(name = "LOGIN_NAME", length = 127)
    private String loginName;

    /**
     * 电话号码
     */
    @Column(name = "TELEPHONE", length = 31)
    private String telephone;

    /**
     * 邮箱地址
     */
    @Column(name = "EMAIL", length = 255)
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
     * 用户状态
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