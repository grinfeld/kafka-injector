package com.mikerusoft.kafka.injector.core.generate.data;

import com.mikerusoft.kafka.injector.core.utils.Utils;

public class TimestampGenerator implements ValueGenerator<Long> {

    private String value;

    public TimestampGenerator(String value) {
        this.value = value;
    }

    @Override
    public Long generate() {
        return Utils.deNull(value, "").length() <= 0 ? System.currentTimeMillis() : GeneratorUtils.getDate(value, GeneratorUtils.DATE_PATTERN);
    }

    @Override
    public Class<Long> getCastTo() {
        return Long.class;
    }

}
