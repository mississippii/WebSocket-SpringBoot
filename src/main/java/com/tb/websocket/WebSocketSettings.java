package com.tb.websocket;

import com.tb.common.WebSocketType;
import lombok.Data;

@Data
public class WebSocketSettings {
    private WebSocketType webSocketType;
    private String uri;
    private int requestBatchSize;

    public WebSocketSettings(WebSocketType webSocketType, String uri, int requestBatchSize) {
        this.webSocketType = webSocketType;
        this.uri = uri;
        this.requestBatchSize = requestBatchSize;
        String typeIndicator = uri.split(":")[0];
        if (typeIndicator.equals("wss")) {
            this.webSocketType = WebSocketType.Wss;
        } else if (typeIndicator.equals("ws")) {
            this.webSocketType = WebSocketType.Ws;
        } else throw new IllegalArgumentException("Unknown websocket type");
    }
}
