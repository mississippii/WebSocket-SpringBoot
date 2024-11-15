package com.tb.websocket;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;


public class JettyWebSocketBuilder implements Session.Listener  {
    private Session session;
    private WebSocketSettings webSocketSettings;
    WebSocketTransport transportImpl;
    public JettyWebSocketBuilder(
                                 WebSocketSettings webSocketSettings){
        //this.transportImpl=transportImpl;
        this.webSocketSettings=webSocketSettings;
        // Use a standard, HTTP/1.1, HttpClient.
        //HttpClient httpClient = new HttpClient();
        //WebSocketClient webSocketClient = new WebSocketClient(httpClient);
        /*try {
            webSocketClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/
        //WebSocketClient webSocketClient = new WebSocketClient();
        // Instantiate and configure HttpClient.
        HttpClient httpClient = new HttpClient();

        // For example, configure a proxy if needed (comment this line if not required).
        // httpClient.getProxyConfiguration().addProxy(new HttpProxy("localhost", 8888));

        // Instantiate WebSocketClient, passing HttpClient to the constructor.
        WebSocketClient webSocketClient = new WebSocketClient(httpClient);


// Configure WebSocketClient, for example:
        webSocketClient.setMaxTextMessageSize(8 * 1024);

// Start WebSocketClient.
        try {
            webSocketClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

// The client-side WebSocket EndPoint that
// receives WebSocket messages from the server.
        ListenerEndPoint clientEndPoint = new ListenerEndPoint();
// The server URI to connect to.
//        URI serverURI = URI.create("ws://domain.com/path");
        URI serverURI = URI.create(this.webSocketSettings.getUri());

// Connect the client EndPoint to the server.
        try {
            CompletableFuture<Session> clientSessionPromise = webSocketClient.connect(clientEndPoint, serverURI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void onWebSocketOpen(Session session)
    {
        // The WebSocket endpoint has been opened.

        // Store the session to be able to send data to the remote peer.
        this.session = session;

        // You may configure the session.
        session.setMaxTextMessageSize(16 * 1024);

        // You may immediately send a message to the remote peer.
        session.sendText("connected", Callback.from(session::demand, Throwable::printStackTrace));
    }

    @Override
    public void onWebSocketText(String message)
    {
        // A WebSocket text message is received.

        // You may echo it back if it matches certain criteria.
        if (message.startsWith("echo:"))
        {
            // Only demand for more events when sendText() is completed successfully.
            session.sendText(message.substring("echo:".length()), Callback.from(session::demand, Throwable::printStackTrace));
        }
        else
        {
            // Discard the message, and demand for more events.
            session.demand();
        }
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        // The WebSocket endpoint failed.

        // You may log the error.
        cause.printStackTrace();

        // You may dispose resources.
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        // The WebSocket endpoint has been closed.

        // You may dispose resources.
    }

}
