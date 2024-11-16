package com.tb.websocket.jetty;

import com.tb.common.Payload;
import com.tb.common.RawPayload;
import com.tb.common.eventDriven.TransportListener;
import com.tb.transport.Transport;
import com.tb.websocket.ConnectionStatus;
import com.tb.websocket.WebSocketSettings;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CompletableFuture;


public class JettyWebSocket implements Transport, Session.Listener {
    private Session session;
    private WebSocketSettings webSocketSettings;
    com.tb.websocket.WebSocketClient webSocketTransport;
    com.tb.websocket.WebSocketClient webSocketClient;
    public JettyWebSocket(WebSocketSettings webSocketSettings,
                          com.tb.websocket.WebSocketClient webSocketTransport){
        this.webSocketSettings=webSocketSettings;
        this.webSocketTransport=webSocketTransport;
        HttpClient httpClient = new HttpClient();
        ClientUpgradeRequest request = createClientUpgradeRequest();
        WebSocketClient webSocketClient = new WebSocketClient(httpClient);
        webSocketClient.setMaxTextMessageSize(8 * 1024);
        try {webSocketClient.start();}
        catch (Exception e) {throw new RuntimeException(e);}
        URI serverURI = URI.create(this.webSocketSettings.getUri());
        try {
            CompletableFuture<Session> clientSessionPromise =
                    webSocketClient.connect(this, serverURI, request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static ClientUpgradeRequest createClientUpgradeRequest() {
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36");
        request.setHeader("Pragma", "no-cache");
        request.setHeader("Cache-Control", "no-cache");
        //request.setHeader("Accept-Encoding", "gzip, deflate, br, zstd");
        request.setHeader("Accept-Language", "en-US,en;q=0.9");
        //request.setHeader("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits");
        return request;
    }


    @Override
    public void onWebSocketOpen(Session session)
    {
        this.session = session;
        webSocketTransport.onConnectionOpen(new RawPayload("Websocket connected."));
        //session.setMaxTextMessageSize(16 * 1024);
        session.demand();
    }

    @Override
    public void onWebSocketText(String message) {
        webSocketTransport.onConnectionMessage(new RawPayload(message));
        System.out.println(message);
        session.demand();
    }
    @Override
    public void onWebSocketError(Throwable cause)
    {
        cause.printStackTrace();
        webSocketTransport.onConnectionError(new RawPayload(cause.getMessage()),
                cause);
    }
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        webSocketTransport.onConnectionClose(new RawPayload(reason), ConnectionStatus.Closed);
    }

    @Override
    public void addListener(TransportListener transportListener) {

    }

    @Override
    public void sendMessage(Payload payload) {

    }

    @Override
    public void connectOrInit() {

    }
}
