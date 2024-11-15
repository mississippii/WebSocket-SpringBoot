package com.tb;

import com.tb.calling.jingle.JingleCallLeg;
import com.tb.calling.jingle.JingleConnector;
import com.tb.calling.verto.VertoConnectParams;
import com.tb.calling.verto.VertoConnector;
import com.tb.common.Delay;
import com.tb.common.WebSocketType;
import com.tb.common.eventDriven.ServicePingParams;
import com.tb.websocket.JettyWebSocketBuilder;
import com.tb.xmpp.component.WeatherComponent;
import com.tb.transport.rest.RestSettings;
import com.tb.transport.xmpp.XmppSettings;
import com.tb.websocket.WebSocketSettings;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.whack.ExternalComponentManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.xmpp.component.ComponentException;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        JettyWebSocketBuilder builder= new JettyWebSocketBuilder(
                new WebSocketSettings(WebSocketType.Ws, "ws://103.248.13.73:8081", 1000)
        );
        //testXmppComponent();
        /*VertoConnectParams params = new VertoConnectParams("09638999999",
                "09638999999asdf",
                new WebSocketSettings(WebSocketType.Ws, "ws://103.248.13.73:8081", 1000),
                new ServicePingParams());
        VertoConnector vc = new VertoConnector(params);
        vc.connectOrInit();
        Delay.sleep(1000);
        vc.login();

        RestSettings restSettings = new RestSettings("http://36.255.71.143:5280/rest");
        XmppSettings xmppSettings = new XmppSettings("36.255.71.143", 5222, "appout",
                "test123", "localhost", "Conversations.restB",
                ConnectionConfiguration.SecurityMode.disabled, 1);
        JingleConnector jingleConnector = new JingleConnector(xmppSettings, restSettings);
        jingleConnector.connectOrInit();
        JingleCallLeg jingleCall = new JingleCallLeg(jingleConnector);
        jingleCall.setVertoConnector(vc);*/
        SpringApplication.run(App.class, args);
    }

    private static void testXmppComponent() {

        final ExternalComponentManager manager = new ExternalComponentManager("36.255.71.143", 8888, false);
        manager.setSecretKey("call.brilliant.com.bd", "whack");
        manager.setMultipleAllowed("call.brilliant.com.bd", true);
        try {
            manager.addComponent("call.brilliant.com.bd", new WeatherComponent("call.brilliant.com.bd", manager.getServerName()));
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (ComponentException e) {
            e.printStackTrace();
        }
    }
}
