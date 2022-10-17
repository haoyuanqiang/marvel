package org.marvel.deevy.common.context;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * 用于从 Spring Context 容器获取 Bean 实例
 *
 * @author haoyuanqiang
 * @date 2022/4/13 10:46
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Component
@Slf4j
public class SpringBootApplicationContext implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 获取Spring上下文对象
     *
     * @return Spring上下文对象
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        if (ObjectUtil.isNull(SpringBootApplicationContext.applicationContext)) {
            SpringBootApplicationContext.applicationContext = applicationContext;
        }
        log.info("ApplicationContext configured successfully!");
    }

    /**
     * 根据name获取bean实例
     *
     * @param name bean 名称
     * @return Bean实例
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 获取Spring容器中属于T类型的唯一的Bean实例
     *
     * @param clazz bean 类型
     * @param <T>   xx
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 获取容器中id为name,并且类型为T的Bean实例
     *
     * @param name  实例名称
     * @param clazz bean 类型
     * @param <T>   xxx
     * @return Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
