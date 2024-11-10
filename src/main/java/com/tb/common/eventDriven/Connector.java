package com.tb.common.eventDriven;

import com.tb.common.eventDriven.RequestAndResponse.Payload;
import com.tb.transport.Transport;


public interface Connector {
    void sendMsgToConnector(Payload payload);

    Payload createServicePingMsg();

    Payload createKeepAliveMsg();

    Transport getTransport();

    Payload createRequestFromPayload(Payload payload);

    void connectOrInit();

    String getSessionId();

    void onServiceStatusChange(ServiceStatus status);

    void addPublicListener(TransportListener listener);

}

