package com.tb.calling.jingle.msgTemplates;

public class ICEResponse {
    public static String createMessage( String apartyWithId, String bpartyWithId, String id) {



        // Construct the XML payload
        String xmlPayload = String.format(
                """
                <iq to="%s" id="%s" from="%s" type="result"/>
                """, apartyWithId, id,bpartyWithId);
        return xmlPayload;

    }
}

