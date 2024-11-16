package com.tb.common;

import com.tb.common.eventDriven.RequestAndResponse.PayloadType;
import com.tb.websocket.ConnectionStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;

public class RawPayload {
    private String data; // Main Payload payload
    private HashMap<String, String> headers= new HashMap<>(); // Additional protocol-specific headers
    private HashMap<String, Object> metadata= new HashMap<>();
    LocalDateTime time;
    ConnectionStatus connectionStatus;
    public RawPayload(String data) {
        this.data=data;
    }
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
