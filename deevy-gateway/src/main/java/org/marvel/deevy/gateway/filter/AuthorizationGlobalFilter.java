package org.marvel.deevy.gateway.filter;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.marvel.deevy.gateway.model.HttpResult;
import org.marvel.deevy.gateway.util.TokenUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

/**
 * @author haoyuanqiang
 * @date 2022/9/8 19:01
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Slf4j
public class AuthorizationGlobalFilter implements GlobalFilter, Ordered {

    /**
     * 匹配公开API接口地址的正则表达式
     * 访问公开的API接口不受权限控制
     * 例如：/service-name/public/xxx
     */
    Pattern publicApiMatcher = Pattern.compile("^/[^/]+/public[\\s\\S]*$");


    /**
     * 匹配私有API接口地址的正则表达式
     * 不可通过Gateway访问私有的API接口
     * 例如：/service-name/private/xxx
     */
    Pattern privateApiMatcher = Pattern.compile("^/[^/]+/private[\\s\\S]*$");


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Visited Url: {}", request.getPath().value());

        String path = request.getPath().value();
        if (ReUtil.isMatch(publicApiMatcher, path)) {
            // 公开接口，无需校验权限
            return chain.filter(exchange);
        } else if (ReUtil.isMatch(privateApiMatcher, path)) {
            // 私有接口，不可透过网关访问
            return handleForbidden(exchange.getResponse());
        } else {
            // 保护接口，需要持有效token访问
            String token = request.getHeaders().getFirst("Authorization");

            if (StrUtil.isBlank(token)) {
                return handleNoToken(exchange.getResponse());
            }

            token = StrUtil.replaceIgnoreCase(token, "Bearer ", "");
            if (!TokenUtil.verify(token)) {
                return handleExpiredToken(exchange.getResponse());
            }

            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {

        return -1;
    }


    /**
     * 无 Token 情况下，返回提示信息
     *
     * @param response 响应对象
     * @return Mono<Void>
     */
    private Mono<Void> handleNoToken(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        HttpResult result = new HttpResult(HttpStatus.UNAUTHORIZED.value(), "No valid token is carried!");
        return response.writeWith(Flux.just(response.bufferFactory().wrap(JSONUtil.toJsonStr(result).getBytes())));
    }


    /**
     * Token 过期情况下，返回提示信息
     *
     * @param response 响应对象
     * @return Mono<Void>
     */
    private Mono<Void> handleExpiredToken(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        HttpResult result = new HttpResult(HttpStatus.UNAUTHORIZED.value(), "A invalid or expired token!");
        return response.writeWith(Flux.just(response.bufferFactory().wrap(JSONUtil.toJsonStr(result).getBytes())));
    }


    /**
     * 访问 Private 请求情况下，返回提示信息
     *
     * @param response 响应对象
     * @return Mono<Void>
     */
    private Mono<Void> handleForbidden(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        HttpResult result = new HttpResult(HttpStatus.FORBIDDEN.value(), "Forbidden to access the private API!");
        result.setSubCode(1);
        return response.writeWith(Flux.just(response.bufferFactory().wrap(JSONUtil.toJsonStr(result).getBytes())));
    }
}
