package com.tb.calling.jingle;

import com.tb.calling.verto.msgTemplates.PingResult;
import com.tb.common.eventDriven.Connector;
import com.tb.common.eventDriven.RequestAndResponse.Enums.TransportPacket;
import com.tb.common.eventDriven.RequestAndResponse.Enums.VertoPacket;
import com.tb.common.eventDriven.RequestAndResponse.Payload;
import com.tb.common.eventDriven.ServiceHealthTracker;
import com.tb.common.eventDriven.ServiceStatus;
import com.tb.common.eventDriven.TransportListener;
import com.tb.common.uniqueIdGenerator.UniqueIntGenerator;
import com.tb.transport.Transport;
import com.tb.transport.rest.RestSettings;
import com.tb.transport.rest.RestTransport;
import com.tb.transport.xmpp.JingleTransportType;
import com.tb.transport.xmpp.XmppSettings;
import com.tb.transport.xmpp.XmppTransport;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Data
public class JingleConnector implements Connector {
    RestTransport restTransport;
    RestSettings restSettings;
    XmppTransport xmppTransport;
    XmppSettings xmppSettings;
    JingleTransportType jingleTransportType;
    ServiceHealthTracker serviceHealthTracker;
    TransportListener privateListener;
    List<TransportListener> publicListeners = new CopyOnWriteArrayList<>();
    UniqueIntGenerator intGenerator = new UniqueIntGenerator(0);
    int pingExpiresInSec = 2;

    public JingleConnector(XmppSettings xmppSettings, RestSettings restSettings) {
        List<TransportListener> xmppListeners = Collections.singletonList(privateListener);
        this.privateListener = createPrivateXmppListener(this);
        this.xmppSettings = xmppSettings;
        this.restSettings = restSettings;
        this.xmppTransport = new XmppTransport(xmppSettings,
                Arrays.asList(this.privateListener));
        this.serviceHealthTracker =
                new ServiceHealthTracker(xmppSettings.servicePingParams, null, this);
        if (xmppSettings.servicePingParams != null) {
            serviceHealthTracker.startServicePingMonitor();
        }
    }

    @Override
    public void addPublicListener(TransportListener publicListener) {
        this.publicListeners.add(publicListener);
    }

    @Override
    public void connectOrInit() {
        this.xmppTransport = new XmppTransport(this.xmppSettings,
                Arrays.asList(this.createPrivateXmppListener(this)));
        this.xmppTransport.connectOrInit();
        this.restTransport = new RestTransport(this.restSettings,
                Arrays.asList(this.createPrivateRestListener(this)),
                this.restSettings.baseUrl);
        this.restTransport.connectOrInit();
    }

    @Override
    public String getSessionId() {
        return null;
    }

    @Override
    public void onServiceStatusChange(ServiceStatus status) {

    }

    @Override
    public Payload createServicePingMsg() {
        return new Payload(intGenerator.getNext().toString(),
                PingResult.createMessage(intGenerator.getNext()), VertoPacket.Ping);
    }

    @Override
    public Payload createKeepAliveMsg() {
        throw new RuntimeException("Method createKeepAliveMsg is not implemented");
    }

    @Override
    public Transport getTransport() {
        return this.xmppTransport;
    }

    @Override
    public Payload createRequestFromPayload(Payload payload) {
        return new Payload(intGenerator.getNext().toString(), payload.getData(),
                VertoPacket.Ping);
    }

    @Override
    public void sendMsgToConnector(Payload payload) {
        HashMap<String, Object> metadata = payload.getMetadata();
        Boolean useRest = (Boolean) metadata.get("useRest");
        if (useRest) {
            this.restTransport.sendMessage(payload);
        } else {
            this.restTransport.sendMessage(payload);
        }
    }

    private TransportListener createPrivateXmppListener(JingleConnector mySelf) {
        return getTransportListener(mySelf);
    }

    @NotNull
    private TransportListener getTransportListener(JingleConnector mySelf) {
        return new TransportListener() {
            @Override
            public void onTransportOpen(Payload payload) {

            }

            @Override
            public void onTransportClose(Payload payload) {
            }

            @Override
            public void onTransportMessage(Payload payload) {
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

    private TransportListener createPrivateRestListener(JingleConnector mySelf) {
        return getTransportListener(mySelf);
    }
}

