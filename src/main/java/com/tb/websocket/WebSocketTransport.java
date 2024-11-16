package com.tb.websocket;

import com.tb.common.Payload;
import com.tb.common.eventDriven.TransportListener;
import com.tb.transport.Transport;
import com.tb.websocket.jetty.JettyWebSocket;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class WebSocketTransport implements Transport, TransportListener,com.tb.websocket.WebSocketClient {

    private final WebSocketSettings webSocketSettings;
    private List<TransportListener> publicListeners = new CopyOnWriteArrayList<>();

    private final AtomicInteger messageCounter = new AtomicInteger(0);
    private final int messageLimit = 2;
    boolean reconnectThresholdReached;

    public WebSocketTransport(WebSocketSettings webSocketSettings) {
        this.webSocketSettings = webSocketSettings;
        var webSocket = new JettyWebSocket(webSocketSettings,this);
    }
    @Override
    public void addListener(TransportListener transportListener) {
        this.publicListeners.add(transportListener);
    }

    public void sendMessage(Payload payload) {
        client.sendMessage(payload);

        // Increment message counter and check limit
        int currentCount = messageCounter.incrementAndGet();
        if (currentCount >= messageLimit) {
            System.out.println("Message limit reached, reconnecting WebSocket...");
            reconnect();
        }
    }

    @Override
    public void connectOrInit() {
        connect();
    }

    private void connect() {
        client.connect(webSocketSettings.getUri());
    }

    public void reconnect() {
        this.reconnectThresholdReached = true;
        client.close();
        messageCounter.set(0);
        this.reconnectThresholdReached = false;
        connect();
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }


}
