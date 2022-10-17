package org.marvel.deevy.gateway.filter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * @author haoyuanqiang
 * @date 2022/10/10 16:30
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Slf4j
public class RealAddressFilter implements GlobalFilter, Ordered {

    private static final String UNKNOWN = "unknown";


    private static final String SEPARATOR = ",";


    private static final String HEADER_X_FORWARDED_FOR = "x-forwarded-for";


    private static final String HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";


    private static final String HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";


    private static String getRealIpAddress(ServerHttpRequest serverHttpRequest) {
        String ipAddress;
        try {
            // 1.根据常见的代理服务器转发的请求ip存放协议，从请求头获取原始请求ip。
            // 值类似于203.98.182.163, 203.98.182.163
            ipAddress = serverHttpRequest.getHeaders().getFirst(HEADER_X_FORWARDED_FOR);
            if (StrUtil.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = serverHttpRequest.getHeaders().getFirst(HEADER_PROXY_CLIENT_IP);
            }
            if (StrUtil.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = serverHttpRequest.getHeaders().getFirst(HEADER_WL_PROXY_CLIENT_IP);
            }

            // 2.如果没有转发的ip，则取当前通信的请求端的ip
            if (StrUtil.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                InetSocketAddress inetSocketAddress = serverHttpRequest.getRemoteAddress();
                if(inetSocketAddress != null) {
                    ipAddress = inetSocketAddress.getAddress().getHostAddress();
                }
            }

            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***"
            if (ipAddress != null) {
                ipAddress = ipAddress.split(SEPARATOR)[0].trim();
            }
        } catch (Exception e) {
            log.error("解析请求IP失败", e);
            ipAddress = "";
        }
        return ipAddress == null ? "" : ipAddress;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (StrUtil.startWith(exchange.getRequest().getPath().value(), "/deevy-foundation/public/security")) {
            String ip = getRealIpAddress(exchange.getRequest());
            log.info("ClientIP={}", ip);
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("Marvel-Real-IP", ip)
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        } else {
            return chain.filter(exchange);
        }
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
