package com.tb.websocket;

import com.tb.common.eventDriven.RequestAndResponse.Payload;
import com.tb.common.eventDriven.TransportListener;
import com.tb.transport.Transport;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Data
public class WebSocketTransport implements Transport {

    private final WebSocketSettings webSocketSettings;
    private final SpringWebSocketBuilder builder;
    private WebSocketSession session = null;
    private List<TransportListener> publicListeners;

    private final AtomicInteger messageCounter = new AtomicInteger(0);
    private final int messageLimit = 2; // Fixed number of messages before reconnect
    boolean reconnectThresholdReached;

    public WebSocketTransport(WebSocketSettings webSocketSettings, List<TransportListener> publicListeners) {
        this.webSocketSettings = webSocketSettings;
        this.publicListeners = publicListeners;
        this.builder = new SpringWebSocketBuilder(this);
    }

    @Override
    public void addListener(TransportListener transportListener) {
        this.publicListeners.add(transportListener);
    }

    public void sendMessage(Payload payload) {
        try {
            if (this.session != null && this.session.isOpen()) {
                this.session.sendMessage(new TextMessage(payload.getData()));

                // Increment message counter and check limit
                int currentCount = messageCounter.incrementAndGet();
                if (currentCount >= messageLimit) {
                    System.out.println("Message limit reached, reconnecting WebSocket...");
                    reconnect();
                }
            } else {
                System.out.println("WebSocket session is not open.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void connectOrInit() {
        connect(); // Initialize the first connection
    }

    private void connect() {
        System.out.println("Connecting to WebSocket...");
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(client, builder, this.webSocketSettings.getUri());
        connectionManager.setAutoStartup(true);
        connectionManager.start();

        while (this.session == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("WebSocket connected.");
    }

    public void reconnect() {
        try {
            this.reconnectThresholdReached=true;
            if (this.session != null && this.session.isOpen()) {
                this.session.close();
            }
            // Reset counter and session
            messageCounter.set(0);
            this.reconnectThresholdReached=false;
            this.session = null;

            // Reconnect
            connect();
        } catch (IOException e) {
            throw new RuntimeException("Error while closing WebSocket session.", e);
        }
    }

    public void onWebSocketConnect(WebSocketSession session) {
        this.session = session;
    }
    public void onWebSocketClose(WebSocketSession session, CloseStatus status,
                                 boolean reconnectThresholdReached){
        if (reconnectThresholdReached==false){//reconnect is in progress already
            this.reconnect();
        }
    }
}

