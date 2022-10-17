package org.marvel.deevy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author haoyuanqiang
 * @date 2022/9/7 16:51
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@SpringBootApplication
@EnableEurekaClient
public class DeevyGatewayApplication {


    public static void main(String[] args) {
        SpringApplication.run(DeevyGatewayApplication.class, args);
    }

}
