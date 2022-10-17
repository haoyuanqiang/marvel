package org.marvel.deevy.common.lock;

import cn.hutool.core.date.DateUnit;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author haoyuanqiang
 * @date 2022/7/20 16:41
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Slf4j
public class InterceptorMutex {

    private static InterceptorMutex instance;


    public static InterceptorMutex getInstance() {
        if (null == instance) {
            instance = new InterceptorMutex();
        }
        return instance;
    }

    private static class MutexLock {
        /**
         * 互斥锁Key值
         */
        public String key;

        /**
         * 互斥锁对象
         */
        public ReentrantLock lock;

        public MutexLock(String key, ReentrantLock lock) {
            this.key = key;
            this.lock = lock;
        }
    }


    private final TimedMutexMap<String, MutexLock> cache;


    private InterceptorMutex() {
        cache = new TimedMutexMap<>(DateUnit.MINUTE.getMillis());
        InterceptorMutex that = this;
        cache.setListener((key, mutexLock) -> {
            log.debug("Remove the mutex lock[{}].", key);
            if (mutexLock != null && mutexLock.lock.isLocked()) {
                try {
                    mutexLock.lock.unlock();
                } catch (Exception e) {
                    log.warn("An exception occurs while removing the mutex lock[{}].", e.getMessage());
                }
            }
        });
    }


    private MutexLock findLock(String key) {
        synchronized (this) {
            MutexLock mutexLock = cache.get(key);
            if (null == mutexLock) {
                mutexLock = new MutexLock(key, new ReentrantLock());
                cache.put(key, mutexLock);
                return mutexLock;
            }
            return mutexLock;
        }
    }


    public void tryLock(String key) {
        log.debug("Start to lock the mutex[{}].", key);
        MutexLock mutexLock = findLock(key);
        mutexLock.lock.lock();
        log.debug("Complete to lock the mutex[{}].", key);
    }


    public void unlock(String key) {
        log.debug("Start to unlock the mutex[{}]", key);
        MutexLock mutexLock = findLock(key);
        if (null != mutexLock && mutexLock.lock.isLocked()) {
            mutexLock.lock.unlock();
        }
        log.debug("Complete to unlock the mutex[{}]", key);
    }
}
