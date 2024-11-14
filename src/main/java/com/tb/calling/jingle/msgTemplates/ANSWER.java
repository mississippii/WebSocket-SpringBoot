package com.tb.calling.jingle.msgTemplates;

import com.tb.common.UUIDGen;
import com.tb.common.uniqueIdGenerator.ShortIdGenerator;

public class ANSWER {
    public static String createMessage(String apartyWithId, String bpartyWithId, String sid, String pwd,String priority,String ip,int port) {
        String xmlPayload = String.format(
                """
                <iq from="%s" to="%s" type="set" id="appoutAnswer">
                    <jingle action="appout-answer" sid="%s" xmlns="urn:xmpp:jingle:1">
                    </jingle>
                </iq>
                """, bpartyWithId, apartyWithId, ShortIdGenerator.getNext(), sid, "answer", pwd,priority,ip,port, UUIDGen.getNextAsStr());

        return xmlPayload;
    }
}
