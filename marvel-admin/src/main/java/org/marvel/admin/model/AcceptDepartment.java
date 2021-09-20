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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.marvel.admin.entity.DepartmentEntity;

import java.util.List;

@Data
public class AcceptDepartment {
    /**
     * ID
     */
    private String id;

    /**
     * 部门编号
     */
    private String code;

    /**
     * 部门姓名
     */
    private String name;

    /**
     * 父级部门ID
     */
    private String parentId;

    /**
     * 部门状态
     */
    private Integer status;

    /**
     * 部门排序码
     */
    private Long sortNumber;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 子部门列表
     */
    private List<AcceptDepartment> children;

    public int validate() {
        int maxLength = 127;
        if (StrUtil.isBlank(code) || StrUtil.length(code) > maxLength) {
            return 1100;
        }
        if (!Validator.isGeneral(code)) {
            return 1101;
        }
        if (StrUtil.isBlank(name) || StrUtil.length(name) > maxLength) {
            return 1102;
        }
        if (!Validator.isGeneralWithChinese(name)) {
            return 1103;
        }
        if (ObjectUtil.isNull(status)) {
            status = 1;
        }
        if (ObjectUtil.isNull(sortNumber)) {
            sortNumber = 0L;
        }
        return 0;
    }

    public DepartmentEntity convertToEntity() {
        DepartmentEntity entity = new DepartmentEntity();
        CopyOptions copyOptions = CopyOptions.create();
        copyOptions.setIgnoreError(true);
        copyOptions.setIgnoreNullValue(true);
        BeanUtil.copyProperties(this, entity, copyOptions);
        return entity;
    }
}
