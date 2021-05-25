package com.binghe.domain;

import java.util.Arrays;

public enum Level {
    BASIC(1), SILVER(2), GOLD(3);

    private final int value;

    Level(int value) {
        this.value = value;
    }

    public int intValue() {
        return value;
    }

    public static Level valueOf(int value) {
        return Arrays.stream(values())
            .filter(level -> level.intValue() == value)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException());
    }
}
