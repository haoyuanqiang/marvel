package org.marvel.deevy.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 键值对数据类型
 *
 * @author haoyuanqiang
 * @date 2022/4/24 10:28
 * @project marvel-deevy
 * @copyright © 2016-2022 MARVEL
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pair<K, V> {

    private K key;

    private V value;


    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}
