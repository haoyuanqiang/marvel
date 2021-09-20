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

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Table(name = "MV_ROLE", indexes = {
        @Index(name = "MV_IN_R_CODE", columnList = "CODE"),
        @Index(name = "MV_IN_R_NAME", columnList = "NAME"),
        @Index(name = "MV_IN_R_VALID", columnList = "VALID")
})
public class RoleEntity extends BaseEntity {
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
