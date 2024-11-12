package com.tb.calling.verto;

import com.tb.calling.verto.msgTemplates.Echo;
import com.tb.calling.verto.msgTemplates.Login;
import com.tb.calling.verto.msgTemplates.PingResult;
import com.tb.common.StringUtil;
import com.tb.common.UUIDGen;
import com.tb.common.eventDriven.Connector;
import com.tb.common.eventDriven.RequestAndResponse.Enums.TransportPacket;
import com.tb.common.eventDriven.RequestAndResponse.Enums.VertoPacket;
import com.tb.common.eventDriven.RequestAndResponse.Payload;
import com.tb.common.eventDriven.ServiceHealthTracker;
import com.tb.common.eventDriven.ServiceStatus;
import com.tb.common.eventDriven.TransportListener;
import com.tb.common.uniqueIdGenerator.UniqueIntGenerator;
import com.tb.transport.Transport;
import com.tb.websocket.WebSocketTransport;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class VertoConnector implements Connector {
    VertoConnectParams params;
    ServiceHealthTracker serviceHealthTracker;
    WebSocketTransport transport;
    TransportListener transportListener;
    List<TransportListener> publicListeners = new CopyOnWriteArrayList<>();
    UniqueIntGenerator intGenerator = new UniqueIntGenerator(0);
    String vertoWebSocketSessionId;
    int pingExpiresInSec = 2;

    public VertoConnector(VertoConnectParams params) {
        this.params = params;
        this.transportListener = createTransportListener(this);
        this.serviceHealthTracker =
                new ServiceHealthTracker(params.servicePingParams, null, this);
        List<TransportListener> listeners = Collections.singletonList(this.transportListener);
        this.transport = new WebSocketTransport(params.webSocketSettings, listeners);
    }

    @Override
    public void addPublicListener(TransportListener publicListener) {
        this.publicListeners.add(publicListener);
    }

    @Override
    public void connectOrInit() {
        List<TransportListener> listeners = new ArrayList<>();
        listeners.add(this.transportListener);
        this.vertoWebSocketSessionId = UUID.randomUUID().toString();
        this.transport.connectOrInit();
    }

    @Override
    public String getSessionId() {
        return this.vertoWebSocketSessionId;
    }

    @Override
    public void onServiceStatusChange(ServiceStatus status) {

    }

    private TransportListener createTransportListener(VertoConnector mySelf) {
        return new TransportListener() {
            @Override
            public void onTransportOpen(Payload payload) {

            }

            @Override
            public void onTransportClose(Payload payload) {
                if (payload.getData().equals("reconnect")) {
                    mySelf.connectOrInit();
                }
            }

            @Override
            public void onTransportMessage(Payload payload) {
                System.out.println("Received Verto:" + payload.getData());
                if (payload.getData().contains("verto.ping")) {//send pong to keep connection up
                    int id = Integer.parseInt(
                            StringUtil.Parser
                                    .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(payload.getData(),
                                            "id", ",").substring(2));
                    Payload pong = new Payload(UUIDGen.getNextAsStr(), PingResult.createMessage(id),
                            VertoPacket.PingResp);
                    mySelf.getTransport().sendMessage(pong);
                    System.out.println("Verto Pong Sent:" + pong.getData());
                }
                if (payload.getPayloadType() == TransportPacket.Payload) {
                    for (TransportListener publicListener : mySelf.getPublicListeners()) {
                        publicListener.onTransportMessage(payload);
                    }
                }
            }

            @Override
            public void onTransportError(Payload payload) {

            }

            @Override
            public void onTransportStatus(Payload payload) {

            }
        };
    }

    public void login() {
        String data =
                Login.createMessage(params.login, params.password,
                        params.webSocketSettings.getUri(),
                        vertoWebSocketSessionId, this.intGenerator.getNext());
        System.out.println(data);
        transport.sendMessage(new Payload(UUID.randomUUID().toString(),
                data, VertoPacket.Login));
    }

    @Override
    public Payload createServicePingMsg() {
        return new Payload(intGenerator.getNext().toString(),
                Echo.createMessage(intGenerator.getNext()), VertoPacket.Ping);
    }

    @Override
    public Payload createKeepAliveMsg() {
        throw new RuntimeException("Method createKeepAliveMsg is not implemented");
    }

    @Override
    public Transport getTransport() {
        return (Transport) this.transport;
    }

    @Override
    public Payload createRequestFromPayload(Payload payload) {
        return new Payload(intGenerator.getNext().toString(), payload.getData(),
                VertoPacket.Ping);
    }

    @Override
    public void sendMsgToConnector(Payload payload) {
        this.transport.sendMessage(payload);
    }
}

