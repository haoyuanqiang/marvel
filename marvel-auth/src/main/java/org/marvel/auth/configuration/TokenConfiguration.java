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

package org.marvel.auth.configuration;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.date.DateUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TokenConfiguration {
    // 获取配置Bean (以代码的方式配置sa-token, 此配置会覆盖yml中的配置)

    @Primary
    @Bean(name = "MySaTokenConfig")
    public SaTokenConfig getSaTokenConfig() {
        SaTokenConfig config = new SaTokenConfig();
        // token名称 (同时也是cookie名称)
        config.setTokenName("token");
        // token有效期，单位s 默认1小时
        config.setTimeout(DateUnit.DAY.getMillis() * 30);
        // token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒
        config.setActivityTimeout(DateUnit.MINUTE.getMillis() * 5);
        // 是否允许同一账号并发登录 (为true时允许一起登录, 为false时新登录挤掉旧登录)
        config.setAllowConcurrentLogin(false);
        // 在多人登录同一账号时，是否共用一个token
        // (为true时所有登录共用一个token, 为false时每次登录新建一个token)
        config.setIsShare(true);
        // token风格
        config.setTokenStyle("random-32");
        return config;
    }
}
