package com.mikerusoft.kafka.injector.core.generate.data;

import com.mifmif.common.regex.Generex;
import com.mikerusoft.kafka.injector.core.utils.Utils;

public class RegexTimestampGenerator implements ValueGenerator<Long> {
    private Generex regex;

    public RegexTimestampGenerator(String regex) {
        if ("". equals(Utils.deNull(regex, ""))) {
            throw new IllegalArgumentException("Regex shouldn't be null");
        }
        this.regex = new Generex(regex);
    }


    @Override
    public Long generate() {
        return GeneratorUtils.getDate(this.regex.random(), GeneratorUtils.DATE_PATTERN);
    }

    @Override
    public Class<Long> getCastTo() {
        return Long.class;
    }
}
