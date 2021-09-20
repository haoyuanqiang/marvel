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

package org.marvel.admin.model;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.List;

@Data
public class AcceptMenu {
    /**
     * ID
     */
    private String id;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 菜单编码
     */
    private String code;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单图标
     */
    private String iconName;

    /**
     * 菜单路径
     */
    private String path;

    /**
     * 菜单路径类型
     */
    private Integer routeType;

    /**
     * 类型: 1-分组  2-页面 3-第三方页面
     */
    private Integer type;

    /**
     * 显示状态：显示、隐藏
     */
    private Integer visible;

    /**
     * 父级菜单
     */
    private String parentId;

    /**
     * 排序
     */
    private Long sortNumber;

    /**
     * 状态：0不可用1可用
     */
    private Integer status;

    /**
     * 子部门列表
     */
    private List<AcceptMenu> children;

    public int validate() {
        int maxLength = 127;
        if (StrUtil.isBlank(code) || StrUtil.length(code) > maxLength) {
            return 1150;
        }
        if (!Validator.isGeneral(code)) {
            return 1151;
        }
        if (StrUtil.isBlank(name) || StrUtil.length(name) > maxLength) {
            return 1152;
        }
        if (!Validator.isGeneralWithChinese(name)) {
            return 1153;
        }
        if (ObjectUtil.isNull(status)) {
            status = 1;
        }
        if (ObjectUtil.isNull(sortNumber)) {
            sortNumber = 0L;
        }
        return 0;
    }
}
