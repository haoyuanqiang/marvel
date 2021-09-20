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

import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@FieldNameConstants
@Table(name = "MV_USER_LOGIN", indexes = {
        @Index(name = "MV_IN_U_NAME", columnList = "NAME"),
        @Index(name = "MV_IN_U_VALID", columnList = "VALID")
})
public class UserLoginEntity extends BaseEntity {

    /**
     * 用户名称
     */
    @Column(name = "NAME", length = 255)
    private String name;

    /**
     * 用户密码
     */
    @Column(name = "PASSWORD", length = 255)
    private String password;

    /**
     * 用户状态：正常，停用
     */
    @Column(name = "STATUS")
    private Integer status;

    /**
     * 加密密码
     *
     * @param password 密码
     * @return 加密后字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.objectId());
    }

    /**
     * 判断密码是否匹配
     *
     * @param password 明文密码
     * @return 判断结果
     */
    public boolean isMatchPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, this.password);
    }
}

