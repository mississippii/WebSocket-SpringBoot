package com.tb.common;

import com.tb.common.eventDriven.RequestAndResponse.PayloadType;
import com.tb.websocket.ConnectionStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;

public class Payload extends RawPayload {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlSuffix() {
        return urlSuffix;
    }

    public void setUrlSuffix(String urlSuffix) {
        this.urlSuffix = urlSuffix;
    }

    public PayloadType getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(PayloadType payloadType) {
        this.payloadType = payloadType;
    }

    String urlSuffix;
    PayloadType payloadType;
    public Payload(String id, String data, PayloadType payloadType) {
        super(data);
        this.id = id;
        this.payloadType=payloadType;
    }
    public Payload(String data,PayloadType payloadType) {
        super(data);
        this.payloadType = payloadType;
    }
}
