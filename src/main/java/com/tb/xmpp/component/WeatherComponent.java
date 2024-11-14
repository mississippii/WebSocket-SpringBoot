package com.tb.xmpp.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.Message;


/**
 * This component provides weather information obtained from http://weather.noaa.gov. Each request
 * will generate an HTTP request to the above URL. The JWeather library was used for getting
 * weather information in the METAR format.<p>
 *
 * Note: This code shouldn't be considered ready for production since it generate an HTTP request
 * for each received request. Therefore, it won't scale much.
 *
 * @author Gaston Dombiak
 */
public class WeatherComponent extends AbstractComponent {

    Logger log = LoggerFactory.getLogger(getClass());

    /**
     * The XMPP domain to which this component is registered to.
     */
    private String serverDomain;

    /**
     * The name of this component.
     */
    private String name;


    /**
     * Create a new component which provides weather information.
     * 
     * @param name The name of this component.
     * @param serverDomain The XMPP domain to which this component is registered to.
     */
    public WeatherComponent(String name, String serverDomain) {
        this.name = name;
        this.serverDomain = serverDomain;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "Weather component - sample component";
    }

    @Override
    public String getDomain() {
        return serverDomain;
    }

    /**
     * Handle a receied message and answer the weather information of the requested station id.
     * The request must be made using Message packets where the body of the message should be the
     * station id.<p>
     *
     * Note: I don't know the list of valid station ids so if you find the list please send it to me
     * so I can add it to this example.
     *
     * @param message the Message requesting information about a certain station id.
     */
    @Override
    protected void handleMessage(Message message) {
        System.out.println("Received message:"+message.toXML());
        // Build the answer
        Message reply = new Message();

        reply.setTo(message.getFrom());
        reply.setFrom(message.getTo());
        reply.setType(message.getType());
        reply.setThread(message.getThread());
        reply.setBody("Reply from External component");
        send(reply);
    }

}
