package com.tty.lib.entity.cache;

import java.util.Objects;

public record PageKey<K>(int pageNum, int pageSize, K queryKey) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageKey<?>(int num, int size, Object key))) return false;
        return pageNum == num
                && pageSize == size
                && Objects.equals(queryKey, key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNum, pageSize, queryKey);
    }
}
