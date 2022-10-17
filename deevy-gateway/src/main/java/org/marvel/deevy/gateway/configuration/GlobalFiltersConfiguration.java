package org.marvel.deevy.gateway.configuration;

import org.marvel.deevy.gateway.filter.AuthorizationGlobalFilter;
import org.marvel.deevy.gateway.filter.RealAddressFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haoyuanqiang
 * @date 2022/9/8 19:07
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Configuration
public class GlobalFiltersConfiguration {

    @Bean
    public GlobalFilter authorizationFilter() {
        return new AuthorizationGlobalFilter();
    }


//    @Bean
//    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(r -> r.path("/deevy-foundation/public/security/**")
//                        .filters(f -> f.filter(new RealAddressFilter())
//                                .addResponseHeader("X-Response-Default-Foo", "Default-Bar"))
//                        .uri("http://httpbin.org:80/get")
//                )
//                .build();
//    }




    @Bean
    public GlobalFilter realAddressFilter() {
        return new RealAddressFilter();
    }
}
