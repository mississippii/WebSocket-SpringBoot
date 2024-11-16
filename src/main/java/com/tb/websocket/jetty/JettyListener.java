package com.tb.websocket.jetty;

import com.tb.websocket.WebSocketTransport;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;

import java.nio.ByteBuffer;

public class JettyListener implements Session.Listener
{
    private Session session;
    private WebSocketTransport webSocketTransport;

    public JettyListener(WebSocketTransport webSocketTransport) {
        this.webSocketTransport = webSocketTransport;
    }


}