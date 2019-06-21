package com.mikerusoft.kafka.injector.core.generate.data;

import com.mifmif.common.regex.Generex;

public class RegexGenerator<T> implements ValueGenerator<T> {

    private Generex regex;
    private Class<T> type;

    public RegexGenerator(Class<T> type, String regex) {
        this.regex = new Generex(regex);
        this.type = type;
    }

    @Override
    public T generate() {
        return type.cast(GeneratorUtils.functions(type).parse(this.regex.random()));
    }

    @Override
    public Class<T> getCastTo() {
        return type;
    }

}
