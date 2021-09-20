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
@Table(name = "MV_PERMISSION", indexes = {
        @Index(name = "MV_IN_PE_CODE", columnList = "CODE"),
        @Index(name = "MV_IN_PE_NAME", columnList = "NAME"),
        @Index(name = "MV_IN_PE_VALID", columnList = "VALID")
})
public class PermissionEntity extends BaseEntity {
    /**
     * 权限编码
     */
    @Column(name = "CODE", length = 255)
    private String code;

    /**
     * 权限名称
     */
    @Column(name = "NAME", length = 127)
    private String name;

    /**
     * 菜单ID
     */
    @Column(name = "MENU_ID", length = 63)
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
