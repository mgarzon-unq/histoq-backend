package ar.edu.unq.lom.histoq.backend.model.image.algorithms.embedded.util;

import lombok.Data;

@Data
public class Pair<K,V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
