package org.example.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.example.eventProducer.UserInfoEvent;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed for now
    }

    @Override
    public byte[] serialize(String topic, UserInfoEvent data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing UserInfoEvent", e);
        }
    }

    // Remove or override correctly to avoid using super.serialize (optional)
    @Override
    public byte[] serialize(String topic, Headers headers, UserInfoEvent data) {
        return serialize(topic, data); // Reuse the existing logic
    }

    @Override
    public void close() {
        // Nothing to close
    }
}
