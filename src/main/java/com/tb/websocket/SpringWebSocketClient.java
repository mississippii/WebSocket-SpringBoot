package com.tb.websocket;

import com.tb.common.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;

public class SpringWebSocketClient implements WebSocketClient {

    private WebSocketSession session;
    private SpringWebSocketBuilder builder;
    private Runnable onConnect;
    private CloseStatus onClose;

    @Autowired
    public SpringWebSocketClient(SpringWebSocketBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void connect(String uri) {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(client, builder, uri);
        connectionManager.setAutoStartup(true);
        connectionManager.start();

        while (this.session == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (onConnect != null) onConnect.run();
    }

    @Override
    public void sendMessage(Payload payload) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(payload.getData()));
            } else {
                throw new RuntimeException("WebSocket session is not open.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    @Override
    public void setOnOpenListener(Runnable onConnect) {
        this.onConnect = onConnect;
    }

    @Override
    public void setOnCloseListener(CloseStatus status) {
        this.onClose = status;
    }

    @Override
    public void setOnMessageListener(Runnable onConnect) {

    }

    @Override
    public void setOnErrorListener(Runnable onConnect) {

    }

    public void onWebSocketConnect(WebSocketSession session) {
        this.session = session;
    }

    public void onWebSocketClose(WebSocketSession session, CloseStatus status) {
        if (onClose != null) {
            setOnCloseListener(status);
        }
    }
}
