package org.marvel.deevy.foundation.scheduler;

/**
 * @author haoyuanqiang
 * @date 2022/10/14 11:19
 * @project marvel-deevy
 * @copyright © 2016-2022 SUPCON
 */
public interface Scheduler extends Runnable {

    void initialize();

    String getCronExpression();


    void destroy();
}
