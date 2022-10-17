package org.marvel.deevy.common.lock;

import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;

/**
 * @author haoyuanqiang
 * @date 2022/7/20 19:08
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
class TimedMutexMap<K, V> extends TimedCache<K, V> {

    private long lastPruneTime;

    public TimedMutexMap(long timeout) {
        super(timeout);
        lastPruneTime = System.currentTimeMillis();
    }


    public V get(K key) {
        synchronized (this) {
            if (System.currentTimeMillis() - lastPruneTime > DateUnit.MINUTE.getMillis()) {
                if (containsKey(key)) {
                    get(key, true);
                }
                pruneCache();
                lastPruneTime = System.currentTimeMillis();
            }
            return get(key, true);
        }
    }

    public void put(K key, V value) {
        synchronized (this) {
            if (System.currentTimeMillis() - lastPruneTime > DateUnit.MINUTE.getMillis()) {
                pruneCache();
                lastPruneTime = System.currentTimeMillis();
            }
            put(key, value, DateUnit.MINUTE.getMillis());
        }
    }

}
