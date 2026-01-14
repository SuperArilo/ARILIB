package com.tty.lib.entity.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityCache<K, T> {

    private final Map<K, T> cache = new ConcurrentHashMap<>();

    public T get(K key) {
        return cache.get(key);
    }

    public void put(K key, T entity) {
        cache.put(key, entity);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}
