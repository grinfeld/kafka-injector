package com.mikerusoft.kafka.injector.core.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

public class JsonSerializer<T> implements Serializer<T> {

    private final ObjectMapper objectMapper;

    public JsonSerializer() {
        objectMapper = new ObjectMapper();
        this.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No-op
    }

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            return null;
        }
        try {
            return this.objectMapper.writeValueAsBytes(data);
        } catch (IOException ex) {
            throw new SerializationException("Can't serialize data [" + data + "] for topic [" + topic + "]", ex);
        }
    }

    @Override
    public void close() {
        // No-op
    }
}
