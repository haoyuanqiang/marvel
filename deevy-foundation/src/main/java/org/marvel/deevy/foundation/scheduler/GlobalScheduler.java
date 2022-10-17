package org.marvel.deevy.foundation.scheduler;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author haoyuanqiang
 * @date 2022/10/14 11:18
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Component
@Slf4j
public class GlobalScheduler {

    private final List<Scheduler> schedulers = new ArrayList<>();

    @PostConstruct
    public void initialize() {
        ThreadUtil.execute(() -> {
            // 延时一分钟启动定时调度
            ThreadUtil.sleep(DateUnit.MINUTE.getMillis());

            log.info("Start initializing scheduled tasks...");
            String schedulerPackage = StrUtil.format("{}.task", ClassUtil.getPackage(GlobalScheduler.class));
            Set<Class<?>> schedulerTypes = ClassUtil.scanPackageBySuper(schedulerPackage, Scheduler.class);
            for (Class<?> schedulerType : schedulerTypes) {
                String className = ClassUtil.getClassName(schedulerType, false);
                Class<? extends Scheduler> type = ClassUtil.loadClass(className);
                Scheduler subscriber = ReflectUtil.newInstanceIfPossible(type);
                try {
                    subscriber.initialize();
                } catch (Exception e) {
                    log.warn("The scheduled task[{}] initialization failed and it will not be executed.", className);
                    log.warn(e.getMessage(), e);
                    continue;
                }
                try {
                    CronUtil.schedule(subscriber.getCronExpression(), subscriber);
                    schedulers.add(subscriber);
                    log.info("The scheduled task[{}] has been added to the task pool.", className);
                } catch (Exception e) {
                    log.warn("The scheduled task[{}] cannot be added to the task pool.", className);
                    log.warn(e.getMessage(), e);
                }
            }
            if (schedulers.size() > 0) {
                // 支持秒级别定时任务
                // CronUtil.setMatchSecond(true);
                CronUtil.start();
            } else {
                log.info("Task scheduling will not start because there are no scheduled tasks to be scheduled.");
            }
            log.info("Complete initializing scheduled tasks.");
        });

    }

    @PreDestroy
    public void destructor() {
        CronUtil.stop();
        for (Scheduler scheduler : schedulers) {
            try {
                scheduler.destroy();
            } catch (Exception e) {
                log.warn("The scheduled task[{}] cannot be destroyed normally.",
                        ClassUtil.getClassName(scheduler, false));
            }
        }
    }
}
