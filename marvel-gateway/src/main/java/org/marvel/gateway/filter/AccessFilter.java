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

package org.marvel.gateway.filter;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.marvel.gateway.model.MarvelResponse;
import org.marvel.gateway.model.VerificationResult;
import org.marvel.gateway.service.FeignAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Slf4j
public class AccessFilter extends ZuulFilter {
    @Resource
    private FeignAuthService feignAuthService;

    /**
     * 返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型：
     * pre：可以在请求被路由之前调用
     * route：在路由请求时候被调用
     * post：在route和error过滤器之后被调用
     * error：处理请求时发生错误时被调用
     *
     * @return 过滤器的类型
     */
    @Override
    public String filterType() {
        // 前置过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        // 过滤器的执行顺序，数字越大优先级越低
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        // 是否执行该过滤器，此处为true，说明需要过滤
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String path = request.getRequestURI();
        if (StrUtil.startWith(path, "/marvel-auth")) {
            return null;
        }
        String method = request.getMethod();
        MarvelResponse<VerificationResult> result = feignAuthService.checkPermission(method, path);
        log.info("checkPermission: {} {} {}", method, path, result);
        // 访问权限认证逻辑
        Boolean authResult = false;
        if (ObjectUtil.isNotNull(result) && ObjectUtil.isNotNull(result.getResult())) {

            if (ObjectUtil.isNull(result.getResult().getLoginId())) {
                authResult = null;
            } else {
                authResult = CompareUtil.compare(result.getResult().getIsPermitted(), true) == 0;
                context.addZuulRequestHeader("username", result.getResult().getLoginId());
            }
        }
        if (ObjectUtil.isNull(authResult) || !authResult) {
            // 过滤该请求，不进行路由
            context.setSendZuulResponse(false);
            Map<String, Object> noAccessResult = new LinkedHashMap<>();
            int httpStatus = 200;
            if (ObjectUtil.isNull(authResult)) {
                context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
                httpStatus = HttpStatus.UNAUTHORIZED.value();
                noAccessResult.put("code", httpStatus);
                noAccessResult.put("message", "Unauthorized Request");
            } else {
                context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
                httpStatus = HttpStatus.FORBIDDEN.value();
                noAccessResult.put("code", httpStatus);
                noAccessResult.put("message", "Forbidden Request");
            }
            // 返回前端内容
            context.setResponseBody(JSONUtil.toJsonStr(noAccessResult));
            HttpServletResponse response = context.getResponse();
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(httpStatus);
        }
        return null;
    }
}