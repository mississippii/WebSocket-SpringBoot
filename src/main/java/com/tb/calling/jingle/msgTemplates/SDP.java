package com.tb.calling.jingle.msgTemplates;

import com.tb.common.uniqueIdGenerator.ShortIdGenerator;
import okhttp3.OkHttpClient;
public class SDP {
    public static String createMessage(String bpartyWithId, String apartyWithId, String sid,
                                       String ssrc,String msid, String ufrag, String pwd,
                                        String fingerprint) {
        String xmlPayload = String.format(
                """
                <iq from="%s" to="%s" type="set" id="%s">
                    <jingle action="session-accept" sid="%s" xmlns="urn:xmpp:jingle:1">
                        <content creator="initiator" name="0">
                            <description media="audio" xmlns="urn:xmpp:jingle:apps:rtp:1">
                                <payload-type name="opus" channels="2" clockrate="48000" id="111">
                                    <parameter value="10" name="minptime"/>
                                    <parameter value="1" name="useinbandfec"/>
                                    <rtcp-fb type="transport-cc" xmlns="urn:xmpp:jingle:apps:rtp:rtcp-fb:0"/>
                                </payload-type>
                                <payload-type name="G722" clockrate="8000" id="9"/>
                                <payload-type name="PCMU" clockrate="8000" id="0"/>
                                <payload-type name="PCMA" clockrate="8000" id="8"/>
                                <payload-type name="CN" clockrate="8000" id="13"/>
                                <payload-type name="telephone-event" clockrate="48000" id="110"/>
                                <payload-type name="telephone-event" clockrate="8000" id="126"/>
                                <rtp-hdrext uri="urn:ietf:params:rtp-hdrext:ssrc-audio-level" id="1" xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0"/>
                                <rtp-hdrext uri="http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time" id="2" xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0"/>
                                <rtp-hdrext uri="http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01" id="3" xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0"/>
                                <rtp-hdrext uri="urn:ietf:params:rtp-hdrext:sdes:mid" id="4" xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0"/>
                                <extmap-allow-mixed xmlns="urn:xmpp:jingle:apps:rtp:rtp-hdrext:0"/>
                                <source ssrc="%s" xmlns="urn:xmpp:jingle:apps:rtp:ssma:0">
                                    <parameter value="d/Vwsf7nhcFMZZRm" name="cname"/>
                                    <parameter value="- audio-track-%s" name="msid"/>
                                </source>
                                <rtcp-mux/>
                            </description>
                            <transport ufrag="%s" xmlns="urn:xmpp:jingle:transports:ice-udp:1" pwd="%s">
                                <fingerprint xmlns="urn:xmpp:jingle:apps:dtls:0" hash="sha-256" setup="active">%s</fingerprint>
                                <trickle xmlns="http://gultsch.de/xmpp/drafts/jingle/transports/ice-udp/option"/>
                                <renomination xmlns="http://gultsch.de/xmpp/drafts/jingle/transports/ice-udp/option"/>
                            </transport>
                        </content>
                        <group xmlns="urn:xmpp:jingle:apps:grouping:0" semantics="BUNDLE">
                            <content name="0"/>
                        </group>
                    </jingle>
                </iq>
                """, bpartyWithId, apartyWithId, ShortIdGenerator.getNext(), sid,ssrc,msid, ufrag, pwd,fingerprint);
        return xmlPayload;
    }
}
