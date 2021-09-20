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

package org.marvel.auth.common;


public interface Constants {
    /**
     * 用户相关常量
     */
    int USER_STATUS_NORMAL = 1;
    int USER_STATUS_BLOCKED = 0;
    /**
     * 各种正则表达式
     */
    String USERNAME_PATTERN = "^[\\u4E00-\\u9FA5\\uf900-\\ufa2d_a-zA-Z][\\u4E00-\\u9FA5\\uf900-\\ufa2d\\w]{1,19}$";
    String MOBILE_PHONE_PATTERN = "^0{0,1}(13[0-9]|15[0-9]|14[0-9]|18[0-9])[0-9]{8}$";
    String EMAIL_PATTERN = "^\\w+([-.]\\w+)*@\\w+([-]\\w+)*\\.(\\w+([-]\\w+)*\\.)*[a-z]{2,3}$";
    /**
     * 菜单类型
     */
    int MENU = 1;
    int BUTTON = 2;
    /**
     * 基本角色
     */
    String BASE_ROLE = "ROLE_USER";
    String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    /**
     * 安全相关
     */
    String MARVEL_LICENSE = "Made By MARVEL";
    String JWT_SIGN_KEY = "soraka";
    /**
     * token请求头名称
     */
    String TOKEN_HEADER = "Authorization";
    String TOKEN_BEARER = "Bearer ";
    /**
     * 查询客户端
     * 参考 JdbcClientDetailsService
     */
    String CLIENT_FIELDS = "client_id, client_secret, resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";
    String BASE_FIND_STATEMENT = "select " + CLIENT_FIELDS + " from sys_oauth_client_details";
    String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by client_id";
    String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";
}
