package com.tb.calling.verto;

import com.tb.common.eventDriven.ServicePingParams;
import com.tb.websocket.WebSocketSettings;
import lombok.Data;

@Data
public class VertoConnectParams {
    String login;
    String password;
    WebSocketSettings webSocketSettings;
    int heartbeatIntervalSec = 1;
    ServicePingParams servicePingParams = null;

    public VertoConnectParams(String login, String password,
                              WebSocketSettings webSocketSettings, ServicePingParams servicePingParams) {
        this.login = login;
        this.password = password;
        this.webSocketSettings = webSocketSettings;
        this.servicePingParams = servicePingParams;
    }

    public ServicePingParams getKeepAliveParams() {
        return servicePingParams;
    }
}
