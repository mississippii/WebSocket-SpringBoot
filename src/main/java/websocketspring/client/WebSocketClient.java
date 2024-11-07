package websocketspring.client;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Component
public class WebSocketClient {

    private static final String WEBSOCKET_URI = "ws://103.248.13.73:8081";
    private final CustomWebSocketHandler handler;

    public WebSocketClient(CustomWebSocketHandler handler) {
        this.handler = handler;
    }

    @PostConstruct
    public void connect() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketConnectionManager connectionManager = new WebSocketConnectionManager(client, handler, WEBSOCKET_URI);
        connectionManager.setAutoStartup(true);
        connectionManager.start();
    }
}
