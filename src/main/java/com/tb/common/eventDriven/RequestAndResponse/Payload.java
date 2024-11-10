package com.tb.common.eventDriven.RequestAndResponse;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
public class Payload {
    String urlSuffix;
    String id;
    PayloadType payloadType;
    private String data; // Main Payload payload
    private HashMap<String, String> headers; // Additional protocol-specific headers
    private HashMap<String, Object> metadata; // Any extra metadata that might be needed
    LocalDateTime time;

    public Payload(String id, String data, PayloadType payloadType) {
        this(data, payloadType);
        this.id = id;
    }

    public Payload(String data, PayloadType payloadType) {
        this.payloadType = payloadType;
        this.data = data;
        this.headers = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }


    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }
}
