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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Data
@FieldNameConstants
@MappedSuperclass
public class BaseEntity implements Serializable, Cloneable {
    /**
     * 主键，ID
     */
    @Id
    @Column(name = "ID", length = 63)
    private String id;

    /**
     * 数据修改时间
     */
    @Column(name = "MODIFY_TIME")
    private Long modifyTime;

    /**
     * 数据创建时间
     */
    @JsonIgnore
    @Column(name = "CREATE_TIME")
    private Long createTime;

    /**
     * 数据有效性，0 表示无效数据，大于 0 表示有效
     */
    @JsonIgnore
    @Column(name = "VALID")
    private Integer valid;
}
