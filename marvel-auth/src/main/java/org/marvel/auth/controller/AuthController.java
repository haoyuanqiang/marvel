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

package org.marvel.auth.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.marvel.auth.model.LoginParameter;
import org.marvel.auth.model.VerificationResult;
import org.marvel.auth.service.PermissionService;
import org.marvel.common.annotation.ResponseFormat;
import org.marvel.common.exception.ExceptionFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {
    @Resource
    private PermissionService permissionService;

    /**
     * 系统登录
     *
     * @param parameter 登录参数
     * @param request   请求对象
     * @param response  响应对象
     * @return Token
     */
    @PostMapping("/login")
    @ResponseFormat
    public Object login(
            @RequestBody LoginParameter parameter,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (StrUtil.isEmpty(parameter.getUsername()) || StrUtil.isEmpty(parameter.getPassword())) {
            throw ExceptionFactory.create(2001);
        }
        String token = request.getHeader("token");
        Object loginId = StpUtil.getLoginIdByToken(token);
        if (ObjectUtil.isNotNull(loginId)) {
            throw ExceptionFactory.create(2009);
        }
        if (!permissionService.checkUser(parameter.getUsername(), parameter.getPassword())) {
            throw ExceptionFactory.create(2002);
        }
        StpUtil.setLoginId(parameter.getUsername());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        tokenInfo.setTokenTimeout(DateUnit.HOUR.getMillis());
        response.setHeader(tokenInfo.getTokenName(), tokenInfo.getTokenValue());
        return tokenInfo;
    }

    @PostMapping("/logout")
    @ResponseFormat
    public void logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        Object loginId = StpUtil.getLoginIdByToken(token);
        if (ObjectUtil.isNull(loginId)) {
            throw ExceptionFactory.create(2006);
        }
        StpUtil.logoutByLoginId(loginId);
    }

    @GetMapping("/loginUser")
    @ResponseFormat
    public Object getLoginUser(HttpServletRequest request) {
        String token = request.getHeader("token");
        Object loginId = StpUtil.getLoginIdByToken(token);
        if (ObjectUtil.isNull(loginId)) {
            throw ExceptionFactory.create(2008);
        }
        return loginId;
    }

    @PostMapping("/verification")
    @ResponseFormat
    public VerificationResult checkPermission(
            @RequestParam String method,
            @RequestParam String path,
            HttpServletRequest request
    ) {
        String token = request.getHeader("token");
        VerificationResult result = new VerificationResult();
        Object loginId = StpUtil.getLoginIdByToken(token);
        result.setLoginId(loginId);
        if (ObjectUtil.isNull(loginId)) {
            result.setIsPermitted(false);
        } else if (CompareUtil.compare(loginId.toString(), "admin") == 0) {
            result.setIsPermitted(true);
        } else {
            String permission = StrUtil.format("{} {}", method.toUpperCase(), path.toLowerCase());
            result.setIsPermitted(permissionService.hasPermission(loginId.toString(), permission));
        }
        return result;
    }
}
