package com.tb.calling.jingle.ConversationsRequests;

import com.tb.common.eventDriven.RequestAndResponse.PayloadType;

public enum JingleMsgType implements PayloadType {
    SDP,
    SDP_RESPONSE,
    ICE,
    ICE_RESPONSE,
    PROPOSE_RESPONSE
}
