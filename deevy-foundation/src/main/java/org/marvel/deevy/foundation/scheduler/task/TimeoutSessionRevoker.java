package org.marvel.deevy.foundation.scheduler.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.marvel.deevy.common.context.SpringBootApplicationContext;
import org.marvel.deevy.common.entity.BaseEntity;
import org.marvel.deevy.foundation.model.SessionRevokerOutput;
import org.marvel.deevy.foundation.scheduler.Scheduler;
import org.marvel.deevy.foundation.service.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author haoyuanqiang
 * @date 2022/10/14 10:22
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
@Slf4j
public class TimeoutSessionRevoker implements Scheduler {

    private SessionService sessionService;


    @Override
    public void initialize() {

    }

    @Override
    public String getCronExpression() {
        return "* 0/1 * * * ? ";
    }

    @Override
    public void destroy() {

    }


    @Override
    public void run() {
        if (null == sessionService) {
            try {
                sessionService = SpringBootApplicationContext.getBean(SessionService.class);
            } catch(Exception ignored) {}
        }
        if (null != sessionService) {
            SessionRevokerOutput output = new SessionRevokerOutput();
            int count = sessionService.revokeTimeoutSession(output);
            log.debug("Complete revoking timeout sessions. {} sessions has been revoked.", count);
            if (count > 0) {
                log.info(
                        "Revoked sessions: {}",
                        CollectionUtil.join(
                                CollectionUtil.map(output.getRevokedSession(), BaseEntity::getId, true),
                                ","
                        )
                );
            }
        } else {
            log.info("There is not a valid SessionService and Skip this task.");
        }
    }
}
