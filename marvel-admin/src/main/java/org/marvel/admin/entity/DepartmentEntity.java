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

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Table(name = "MV_DEPARTMENT", indexes = {
        @Index(name = "MV_IN_D_CODE", columnList = "CODE"),
        @Index(name = "MV_IN_D_NAME", columnList = "NAME"),
        @Index(name = "MV_IN_D_PARENT_ID", columnList = "PARENT_ID"),
        @Index(name = "MV_IN_D_VALID", columnList = "VALID")
})
public class DepartmentEntity extends BaseEntity {
    /**
     * 部门编号
     */
    @Column(name = "CODE", length = 255)
    private String code;

    /**
     * 部门姓名
     */
    @Column(name = "NAME", length = 255)
    private String name;

    /**
     * 父级部门ID
     */
    @Column(name = "PARENT_ID", length = 255)
    private String parentId;

    /**
     * 状态：正常，停用
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 部门排序码
     */
    @Column(name = "SORT_NUMBER")
    @OrderBy
    private Long sortNumber;

    /**
     * 子部门集合，数据库不保存此字段
     */
    @Transient
    private List<DepartmentEntity> children;

    public void addChild(DepartmentEntity department) {
        if (ObjectUtil.isNull(children)) {
            children = new ArrayList<>();
        }
        children.add(department);
    }
}
