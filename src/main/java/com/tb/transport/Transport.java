package com.tb.transport;


import com.tb.common.Payload;
import com.tb.common.eventDriven.TransportListener;

public interface Transport {
    void addListener(TransportListener transportListener);
    void sendMessage(Payload payload);
    void connectOrInit();
}
