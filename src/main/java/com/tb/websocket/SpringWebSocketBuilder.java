package com.tb.websocket;

import com.tb.common.eventDriven.RequestAndResponse.Enums.TransportPacket;
import com.tb.common.eventDriven.RequestAndResponse.Payload;
import com.tb.common.eventDriven.TransportListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

public class SpringWebSocketBuilder extends TextWebSocketHandler {

    WebSocketTransport connectionManager;

    public SpringWebSocketBuilder(WebSocketTransport connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        try {
            for (TransportListener publicListener : this.connectionManager.getPublicListeners()) {
                publicListener.onTransportOpen(
                        new Payload(UUID.randomUUID().toString(), "connected", TransportPacket.TransportUp));
            }
            this.connectionManager.onWebSocketConnect(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        for (TransportListener publicListener : connectionManager.getPublicListeners()) {
            publicListener.onTransportMessage(new Payload(UUID.randomUUID().toString(),
                    message.getPayload(), TransportPacket.Payload));
        }
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, Throwable exception) throws Exception {
        try {
            for (TransportListener publicListener : this.connectionManager.getPublicListeners()) {
                publicListener.onTransportError(
                        new Payload(UUID.randomUUID().toString(), "Websocket error. " + exception.getMessage()
                                , TransportPacket.TransportError));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, CloseStatus status) throws Exception {
        for (TransportListener publicListener : this.connectionManager.getPublicListeners()) {
            publicListener.onTransportClose(
                    new Payload(UUID.randomUUID().toString(), "Websocket closed. Reconnect", TransportPacket.TransportDown));
        }
    }
}