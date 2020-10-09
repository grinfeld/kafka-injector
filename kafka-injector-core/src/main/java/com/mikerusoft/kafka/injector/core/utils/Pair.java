package com.mikerusoft.kafka.injector.core.utils;

import lombok.*;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder(builderClassName = "Builder", builderMethodName = "of")
public class Pair<L, R> {

    public static <L,R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    private L left;
    private R right;
}
