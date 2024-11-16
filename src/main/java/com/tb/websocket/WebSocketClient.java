package com.tb.websocket;
import com.tb.common.Payload;
import com.tb.common.RawPayload;

public interface WebSocketClient {
    void connect(String uri);
    void sendMessage(Payload payload);
    void close();
    boolean isConnected();
    void onConnectionOpen(RawPayload rawPayload);
    void onConnectionClose(RawPayload rawPayload, ConnectionStatus connectionStatus);
    void onConnectionMessage(RawPayload rawPayload);
    void onConnectionError(RawPayload rawPayload,Throwable cause);
}
