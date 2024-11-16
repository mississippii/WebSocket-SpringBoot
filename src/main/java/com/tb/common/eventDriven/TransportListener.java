package com.tb.common.eventDriven;
import com.tb.common.Payload;
import com.tb.common.RawPayload;
import com.tb.websocket.ConnectionStatus;

public interface TransportListener {

void onTransportOpen(RawPayload payload);

void onTransportClose(RawPayload payload, ConnectionStatus connectionStatus);

void onTransportMessage(RawPayload payload);

void onTransportError(RawPayload payload);

}