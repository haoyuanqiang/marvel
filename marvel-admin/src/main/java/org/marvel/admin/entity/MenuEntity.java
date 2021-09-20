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
@Table(name = "MV_MENU", indexes = {
        @Index(name = "MV_IN_ME_CODE", columnList = "CODE"),
        @Index(name = "MV_IN_ME_NAME", columnList = "NAME"),
        @Index(name = "MV_IN_ME_VALID", columnList = "VALID")
})
public class MenuEntity extends BaseEntity {
    public static int TYPE_DIRECTORY = 1;

    public static int TYPE_PAGE = 2;

    public static int VISIBLE_SHOW = 1;

    public static int VISIBLE_HIDE = 2;

    public static int STATUS_ENABLE = 1;

    public static int STATUS_DISABLE = 2;

    /**
     * 菜单编码
     */
    @Column(name = "CODE", length = 255)
    private String code;

    /**
     * 菜单名称
     */
    @Column(name = "NAME", length = 127)
    private String name;

    /**
     * 菜单图标
     */
    @Column(name = "ICON", length = 511)
    private String iconName;

    /**
     * 菜单路径
     */
    @Column(name = "PATH", length = 1023)
    private String path;

    /**
     * 菜单路径类型
     */
    @Column(name = "ROUTE_TYPE")
    private Integer routeType;

    /**
     * 类型: 1-分组  2-页面
     */
    @Column(name = "TYPE")
    private Integer type;

    /**
     * 显示状态：显示、隐藏
     */
    @Column(name = "VISIBLE")
    private Integer visible;

    /**
     * 父级菜单
     */
    @Column(name = "PARENT_ID", length = 63)
    private String parentId;

    /**
     * 排序
     */
    @Column(name = "SORT_NUMBER")
    private Long sortNumber;

    /**
     * 状态：0不可用1可用
     */
    @Column(name = "STATUS")
    private Integer status;

    @Transient
    private List<MenuEntity> children;

    public void appendChild(MenuEntity permission) {
        if (ObjectUtil.isNull(children)) {
            children = new ArrayList<>();
        }
        children.add(permission);
    }
}
