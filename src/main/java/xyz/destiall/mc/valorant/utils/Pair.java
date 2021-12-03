package xyz.destiall.mc.valorant.utils;

import java.util.Map;

public class Pair<K,V> implements Map.Entry<K,V> {
    private final K key;
    private V value;
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        if (getValue() != null) {
            V val = getValue();
            this.value = value;
            return val;
        }
        return null;
    }
}
