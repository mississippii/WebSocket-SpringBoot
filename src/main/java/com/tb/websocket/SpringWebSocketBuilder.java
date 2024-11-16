package com.tb.websocket;

import com.tb.common.eventDriven.RequestAndResponse.Enums.TransportPacket;
import com.tb.common.Payload;
import com.tb.common.eventDriven.TransportListener;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

public class SpringWebSocketBuilder extends TextWebSocketHandler {
    private final WebSocketTransport webSocketTransport;
    public SpringWebSocketBuilder(WebSocketTransport webSocketTransport) {
        this.webSocketTransport = webSocketTransport;
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        try {
            notifyListenersOnOpen();
            webSocketTransport.onConnectionOpen(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        webSocketTransport.onConnectionPayload(message);
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, Throwable exception) {
        try {
            String errorMessage = "WebSocket error: " + exception.getMessage();
            Payload errorPayload = new Payload(
                    UUID.randomUUID().toString(),
                    errorMessage,
                    TransportPacket.TransportError
            );
            webSocketTransport.onConnectionOpen(session);
        } catch (Exception e) {
            System.err.println("Error while handling transport error: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, CloseStatus status) {
        try {
            Payload closePayload = new Payload(
                    UUID.randomUUID().toString(),
                    "WebSocket closed with status: " + status.getReason(),
                    TransportPacket.TransportDown
            );
            webSocketTransport.onConnectionClose(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyListenersOnOpen() {
        Payload openPayload = new Payload(
                UUID.randomUUID().toString(),
                "WebSocket connection established",
                TransportPacket.TransportUp
        );
        for (TransportListener listener : webSocketTransport.getPublicListeners()) {
            listener.onTransportOpen(openPayload);
        }
    }


}
