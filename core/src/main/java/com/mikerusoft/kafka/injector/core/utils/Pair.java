package com.mikerusoft.kafka.injector.core.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Pair<L, R> {

    public static <L,R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    private L left;
    private R right;

    public void setKey(L key) {
        this.left = key;
    }

    public void setValue(R value) {
        this.right = value;
    }
}
