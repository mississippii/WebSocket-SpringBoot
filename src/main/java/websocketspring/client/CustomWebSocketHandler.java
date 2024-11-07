package websocketspring.client;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
        new Thread(() -> {
            int messageCount = 1;
            while (session.isOpen()) {
                try {
                    String messageToSend = "Message " + messageCount + " from WebSocket client!";
                    session.sendMessage(new TextMessage(messageToSend)); // Send message to server
                    System.out.println("Message sent: " + messageToSend);
                    messageCount++;
                    Thread.sleep(5000);
                } catch (Exception e) {
                    System.err.println("Failed to send message: " + e.getMessage());
                }
            }
        }).start();
    }

    @Override
    protected void handleTextMessage(org.springframework.web.socket.WebSocketSession session, TextMessage message) throws Exception {

        System.out.println("Received message: " + message.getPayload()+" "+session.getId()); //received message from server
    }

    @Override
    public void handleTransportError(org.springframework.web.socket.WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("Transport error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(org.springframework.web.socket.WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Connection closed: " + status.getReason());
    }
    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message){
        System.out.println("Binary Message received: " + message.getPayload()+" "+session.getId());
    }
}