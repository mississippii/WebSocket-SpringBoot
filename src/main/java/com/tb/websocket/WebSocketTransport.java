package com.tb.websocket;

import com.tb.common.eventDriven.RequestAndResponse.Payload;
import com.tb.common.eventDriven.TransportListener;
import com.tb.transport.Transport;
import lombok.Data;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.List;


@Data
public class WebSocketTransport implements Transport {

    private final WebSocketSettings webSocketSettings;
    private final SpringWebSocketBuilder builder;
    private WebSocketSession session = null;
    List<TransportListener> publicListeners;

    public WebSocketTransport(WebSocketSettings webSocketSettings, List<TransportListener> publicListeners) {
        this.webSocketSettings = webSocketSettings;
        this.publicListeners = publicListeners;
        this.builder = new SpringWebSocketBuilder(this);
    }


    @Override
    public void addListener(TransportListener transportListener) {

    }

    public void sendMessage(Payload payload) {
        try {
            this.session.sendMessage(new TextMessage(payload.getData()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void connectOrInit() {
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
    }

    public void onWebSocketConnect(WebSocketSession session) {
        this.session = session;
    }
}
