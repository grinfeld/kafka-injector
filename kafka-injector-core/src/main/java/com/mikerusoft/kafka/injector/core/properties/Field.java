package com.mikerusoft.kafka.injector.core.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
public class Field {
    private String name;
    @JsonIgnore private GeneratorType type;
    @JsonIgnore private Class<?> cast;
    private String value;
    @JsonIgnore
    @Builder.Default
    private String uid = UUID.randomUUID().toString();
    private Field[] nestedFields;
    @JsonProperty("creator")
    private FieldContainerCreator fieldContainerCreator;

    public Field(String name, GeneratorType type, Class<?> cast, String value, String uid, Field[] nestedFields, FieldContainerCreator fieldContainerCreator) {
        this.name = name;
        this.type = type;
        this.cast = cast;
        this.value = value;
        this.nestedFields = nestedFields;
        this.fieldContainerCreator = fieldContainerCreator;
        this.setUid(uid);
    }

    public Field(String name, GeneratorType type, Class<?> cast, String value, String uid, FieldContainerCreator fieldContainerCreator) {
        this.name = name;
        this.type = type;
        this.cast = cast;
        this.value = value;
        this.fieldContainerCreator = fieldContainerCreator;
        this.setUid(uid);
    }

    void setUid(String uid) {
        this.uid = Optional.ofNullable(uid).orElseGet(() -> UUID.randomUUID().toString());
    }

    @JsonProperty("type")
    public String getTypeString() {
        return type.name();
    }

    @JsonProperty("type")
    public void setTypeString(String type) {
        this.type = GeneratorType.byString(type);
    }

    @JsonProperty("cast")
    public String getCastString() {
        return cast.getName();
    }

    @JsonProperty("cast")
    public void setCastString(String cast) {
        try {
            this.cast = Class.forName(cast);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to find class " + cast, e);
        }
    }

    @JsonProperty("nested-fields")
    public void setNestedFieldsNonDashed(Field[] nestedFields) {
        this.nestedFields = nestedFields;
    }
}
