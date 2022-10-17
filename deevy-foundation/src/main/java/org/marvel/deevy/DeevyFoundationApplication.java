package org.marvel.deevy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author haoyuanqiang
 * @date 2022/9/6 17:19
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@EnableEurekaClient
@SpringBootApplication
public class DeevyFoundationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeevyFoundationApplication.class, args);
    }
}
