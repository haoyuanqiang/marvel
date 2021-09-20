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
@Table(name = "MV_RELATION_ROLE_USER", indexes = {
        @Index(name = "MV_IN_RU_ROLE_ID", columnList = "ROLE_ID"),
        @Index(name = "MV_IN_RU_USER_ID", columnList = "USER_ID"),
        @Index(name = "MV_IN_RU_VALID", columnList = "VALID")
})
public class RelationRoleUserEntity extends BaseEntity {
    /**
     * 岗位ID
     */
    @Column(name = "ROLE_ID", length = 63)
    private String roleId;

    /**
     * 用户ID
     */
    @Column(name = "USER_ID", length = 63)
    private String userId;
}
