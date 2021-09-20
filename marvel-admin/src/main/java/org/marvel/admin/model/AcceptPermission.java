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

@Data
public class AcceptPermission {
    /**
     * ID
     */
    private String id;

    /**
     * 更新时间
     */
    private Long modifyTime;

    /**
     * 权限编码
     */
    private String code;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 菜单ID
     */

    private String menuId;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 权限路径
     */
    private String route;

    /**
     * 排序
     */
    private Integer sortNumber;

    public int validate() {
        int maxLength = 127;
        if (StrUtil.isBlank(code) || StrUtil.length(code) > maxLength) {
            return 1160;
        }
        if (!Validator.isGeneral(code)) {
            return 1161;
        }
        if (StrUtil.isBlank(name) || StrUtil.length(name) > maxLength) {
            return 1162;
        }
        if (!Validator.isGeneralWithChinese(name)) {
            return 1163;
        }
        if (ObjectUtil.isNull(sortNumber)) {
            sortNumber = 0;
        }
        return 0;
    }
}
