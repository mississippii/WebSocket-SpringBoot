package com.tb.calling.jingle;

import com.tb.calling.*;
import com.tb.calling.jingle.ConversationsRequests.JingleICE;
import com.tb.calling.jingle.ConversationsRequests.JingleMsgType;
import com.tb.calling.jingle.ConversationsRequests.JingleSDP;
import com.tb.calling.jingle.ConversationsRequests.ProposeResponse;
import com.tb.calling.jingle.msgTemplates.*;
import com.tb.calling.verto.VertoConnector;
import com.tb.common.Delay;
import com.tb.common.StringUtil;
import com.tb.common.UUIDGen;
import com.tb.common.eventDriven.RequestAndResponse.Enums.TransportPacket;
import com.tb.common.eventDriven.RequestAndResponse.MultiThreadedRequestHandler;
import com.tb.common.eventDriven.RequestAndResponse.Payload;
import com.tb.common.uniqueIdGenerator.ShortIdGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JingleCallLeg extends AbstractCallLeg {
    SDPMessageFactory sdpMessageFactory;
    VertoCallLeg vertoCall;
    MultiThreadedRequestHandler multiThreadedRequestHandler;

    public JingleConnector getJingleConnector() {
        return jingleConnector;
    }

    JingleConnector jingleConnector;

    public JingleSdpParamA getJingleSdpParamA() {
        return jingleSdpParamA;
    }

    JingleSdpParamA jingleSdpParamA;

    public VertoConnector getVertoConnector() {
        return vertoConnector;
    }

    public void setVertoConnector(VertoConnector vertoConnector) {
        this.vertoConnector = vertoConnector;
    }

    final List<JingleICE> jingleIceCandidates = new ArrayList<>();

    VertoConnector vertoConnector;

    public JingleCallLeg(JingleConnector connector) {
        super(connector);
        connector.addPublicListener(this);
        this.jingleConnector = connector;
        this.multiThreadedRequestHandler =
                new MultiThreadedRequestHandler(this.getJingleConnector().restTransport);

    }

    public void sendJingleIceResults() {
        for (JingleICE jingleIceCandidate : jingleIceCandidates) {
            this.multiThreadedRequestHandler.sendResponse(jingleIceCandidate);
        }
    }

    @Override
    public void onStart(Object message) {

    }

    @Override
    public void onNewMessage(Object message) throws IOException {

    }

    @Override
    public void startSession() {

    }

    @Override
    public void updateSession() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void onRing() {

    }

    @Override
    public void onAnswer() {

    }

    @Override
    public void startRing() {

    }


    @Override
    public String extractSdp(String sdp) {
        return null;
    }

    @Override
    public String extractSdpIpAndPort(String sdp) {
        return null;
    }

    @Override
    public void onTransportOpen(Payload payload) {

    }

    @Override
    public void onTransportClose(Payload payload) {

    }

    @Override
    public void onTransportMessage(Payload data) {
        String msg = data.getData();
        if (msg.contains("jm-propose") && !msg.contains("callping")) {
            this.callState = CallState.SESSION_START;

            String aPartyWithDevice = StringUtil.Parser
                    .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(msg, "from='", "' id='");
            String[] tempArr = aPartyWithDevice.split("/");
            this.setaParty(tempArr[0]);
            this.setaPartyDeviceId(tempArr[1]);

            this.setbParty(StringUtil.Parser
                    .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(msg, "to='", "' from='"));
            this.setbPartyDeviceId(this.jingleConnector.xmppSettings.deviceId);


            // Extract the ID from the message
            this.setUniqueId(StringUtil.Parser
                    .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(msg, "id='jm-propose-", "' type='chat'"));

            this.setPhoneNumber(StringUtil.Parser
                    .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(msg, "phone='+88", "@localhost'"));


            this.ringing();
            this.proposeResponse(msg);
            Delay.sleep(1000);
            this.accept();
            this.proceed();
        }
        if (msg.contains("jm-propose") && msg.contains("callping")) {//call-ping
            //this.setCallPing(StringUtil.Parser
            //      .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(msg, "media='audio' ", "='true'"));
            String aPartyWithDevice = StringUtil.Parser
                    .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(msg, "from='", "' id='");
            String[] tempArr = aPartyWithDevice.split("/");
            String aParty = tempArr[0];
            String bParty = StringUtil.Parser
                    .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(msg, "to='", "' from='");
            this.sendCallPingResp(aParty, bParty);
        }
        if (msg.contains("session-initiate")) {

            jingleSdpParamA = new JingleSdpParamA(msg);

            JingleSDP jingleSDP = new JingleSDP(msg, JingleMsgType.SDP);
            assert (!this.getaParty().isEmpty() && !this.getaPartyDeviceId().isEmpty());
            assert (!this.getbParty().isEmpty() && !this.getbPartyDeviceId().isEmpty());
            jingleSDP.getMetadata().put("bParty", this.getbParty() + "/" + this.getbPartyDeviceId());
            jingleSDP.getMetadata().put("aParty", this.getaParty() + "/" + this.getaPartyDeviceId());
            this.multiThreadedRequestHandler.sendResponse(jingleSDP);
        }

        if (msg.contains("transport-info")) {//on ice

            String id = StringUtil.Parser
                    .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(msg, "priority=&apos;", "&apos;");
            setPriorityId(id);

            JingleICE jingleICE = new JingleICE(msg, JingleMsgType.ICE);
            assert (!this.getaParty().isEmpty() && !this.getaPartyDeviceId().isEmpty());
            assert (!this.getbParty().isEmpty() && !this.getbPartyDeviceId().isEmpty());
            jingleICE.getMetadata().put("bParty", this.getbParty() + "/" + this.getbPartyDeviceId());
            jingleICE.getMetadata().put("aParty", this.getaParty() + "/" + this.getaPartyDeviceId());
            this.jingleIceCandidates.add(jingleICE);
            if (this.callState == CallState.SESSION_START) {
                this.callState = CallState.CALLER_SDP_RECEIVED;
            }
            if (this.callState == CallState.CALLER_SDP_RECEIVED) {
                int portIndex = msg.indexOf("port=&apos;") + "port=&apos;".length();
                String subStr = msg.substring(portIndex);
                int port = Integer.parseInt(subStr.split("&")[0]);
                if (port <= 0)
                    throw new RuntimeException("Media Port must be >0 ");
                int ipIndex = msg.indexOf("ip=&apos;") + "ip=&apos;".length();
                subStr = msg.substring(ipIndex);
                String ip = subStr.split("&")[0];

                ICECandidate candidate = new ICECandidate(ShortIdGenerator.getNext(), ip,
                        port, CandidateType.HOST, TransportProtocol.UDP);
                this.vertoCall = new VertoCallLeg(this.vertoConnector);
                this.vertoCall.setUniqueId(UUIDGen.getNextAsStr());
                this.vertoCall.setaParty("09646888888");
                this.vertoCall.setbParty("8801754105098");
                this.vertoCall.addRemoteIceCandidate(candidate);
                this.vertoCall.getConnector().addPublicListener(this.vertoCall);
                this.vertoCall.setJingleLeg(this);
                this.vertoCall.startSession();
                this.callState = CallState.WAITING_RINGING;
            }
        }
        if (msg.contains("session-terminate")) {
            this.vertoCall.sendHangup();
        }
    }

    private void proposeResponse(String msg) {
        String id = StringUtil.Parser
                .getFirstOccuranceOfParamValueByIndexAndTerminatingStr(msg, "id=",
                        "readToEndOfStr");
        ProposeResponse proposeResponse =
                new ProposeResponse(msg, JingleMsgType.PROPOSE_RESPONSE);
        assert (!this.getaParty().isEmpty() && !this.getaPartyDeviceId().isEmpty());
        assert (!this.getbParty().isEmpty() && !this.getbPartyDeviceId().isEmpty());
        proposeResponse.getMetadata().put("bParty", this.getbParty() + "/" + this.getbPartyDeviceId());
        proposeResponse.getMetadata().put("aParty", this.getaParty() + "/" + this.getaPartyDeviceId());
        this.multiThreadedRequestHandler.sendResponse(proposeResponse);
    }

    public void sendIceCandidates() {
        for (ICECandidate candidate : this.remoteIceCandidates.values()) {
            String ip = candidate.getIpAddress();
            int port = candidate.getPort();
            /*String ice = ICE1.createMessage(getaParty()+"/"+getaPartyDeviceId(),
                    getbParty()+"/"+getbPartyDeviceId(),this.getUniqueId(),
                    ip, port,this.getPriorityId());*/
            String ice1 = this.sdpMessageFactory.createICEMessage();
            Payload payload = new Payload(UUIDGen.getNextAsStr(), ice1, TransportPacket.Payload);
            payload.getMetadata().put("useRest", true);
            this.getConnector().sendMsgToConnector(payload);
        }
    }

    public void sendSdp() {

        this.sdpMessageFactory = new SDPMessageFactory(this.getbParty() + "/" + this.getbPartyDeviceId(),
                this.getaParty() + "/" + this.getaPartyDeviceId(),
                this.getUniqueId(),
                vertoCall.getVertoSdpParamA().getSsrc(),
                vertoCall.getVertoSdpParamA().getMsid(),
                vertoCall.getVertoSdpParamA().getUfrag(),
                vertoCall.getVertoSdpParamA().getPwd(),
                vertoCall.getVertoSdpParamA().getFingerprint(), getPriorityId(),
                vertoCall.getVertoSdpParamA().getIp(), vertoCall.getVertoSdpParamA().getPort());
        /*String sdp = SDP.createMessage(getaParty()+"/"+getaPartyDeviceId(),
                getbParty()+"/"+getbPartyDeviceId(),this.getUniqueId());*/
        String sdp = this.sdpMessageFactory.createSDPMessage();
        //String sdp = this.sdpMessageFactory.createSDPWithICEMessage();
//        String sdp = "Hello SDP";
        Payload s = new Payload(UUIDGen.getNextAsStr(), sdp, TransportPacket.Payload);
        s.getMetadata().put("useRest", true);
        this.getConnector().sendMsgToConnector(s);
    }

    public void proceed() {
        String proceed = Proceed.createMessage(getaParty() + "/" + getaPartyDeviceId(),
                getbParty() + "/" + getbPartyDeviceId(), this.getUniqueId());
        Payload p = new Payload(UUIDGen.getNextAsStr(), proceed, TransportPacket.Payload);
        p.getMetadata().put("useRest", true);
        this.getConnector().sendMsgToConnector(p);
    }

    public void endJingleA() {
        String endjinglecall = END.createMessage(getaParty() + "/" + getaPartyDeviceId(),
                getbParty() + "/" + getbPartyDeviceId(), getUniqueId());
        Payload p = new Payload(UUIDGen.getNextAsStr(), endjinglecall, TransportPacket.Payload);
        p.getMetadata().put("useRest", true);
        this.getConnector().sendMsgToConnector(p);
    }

    public void finishjingleA() {
        String finish = FINISH.createMessage(getaParty() + "/" + getaPartyDeviceId(),
                getbParty() + "/" + getbPartyDeviceId(), getUniqueId());
        Payload p = new Payload(UUIDGen.getNextAsStr(), finish, TransportPacket.Payload);
        p.getMetadata().put("useRest", true);
        this.getConnector().sendMsgToConnector(p);
    }
    @Override
    public void answer() {
        String answer = ANSWER.createMessage(getaParty() + "/" + getaPartyDeviceId(),
                getbParty() + "/" + getbPartyDeviceId(),this.getUniqueId(),"answer",this.getPriorityId(),"1.1.1.1", 0);
        Payload p = new Payload(UUIDGen.getNextAsStr(), answer, TransportPacket.Payload);
        p.getMetadata().put("useRest", true);
        this.getConnector().sendMsgToConnector(p);
    }
    public void retarct() {
        String retract = RETRACT.createMessage(getaParty(),
                getbParty() + "/" + getbPartyDeviceId(), getUniqueId());
        Payload p = new Payload(UUIDGen.getNextAsStr(), retract, TransportPacket.Payload);
        p.getMetadata().put("useRest", true);
        this.getConnector().sendMsgToConnector(p);
    }

    public void accept() {
        // Call Accept class and pass extractedId
        String accept = Accept.createMessage(getbParty() + "/" + getbPartyDeviceId(), getbParty(), this.getUniqueId());
        Payload p = new Payload(UUIDGen.getNextAsStr(), accept, TransportPacket.Payload);
        p.getMetadata().put("useRest", true);
        this.getConnector().sendMsgToConnector(p);
    }

    public void sendCallPingResp(String aParty, String bParty) {
        // Call Accept class and pass extractedId
        String resp = Presence.createMessage(aParty, bParty);
        Payload p = new Payload(UUIDGen.getNextAsStr(), resp, TransportPacket.Payload);
        p.getMetadata().put("useRest", true);
        this.getConnector().sendMsgToConnector(p);
    }

    public void ringing() {
        // Call Accept class and pass extractedId
        String ringing = Ringing.createMessage(getbParty() + "/" + getbPartyDeviceId(), getaParty() + "/" + getaPartyDeviceId(), this.getUniqueId());
        Payload p = new Payload(UUIDGen.getNextAsStr(), ringing, TransportPacket.Payload);
        p.getMetadata().put("useRest", true);
        this.getConnector().sendMsgToConnector(p);
    }

//    public void proposeResponse() {
//        // Call Accept class and pass extractedId
//        String proposeResponse= ProposeResponse.createMessage( getbParty()+"/"+getbPartyDeviceId(), getbParty(), this.getUniqueId());
//        Payload p= new Payload(UUIDGen.getNextAsStr(),proposeResponse, TransportPacket.Payload);
//        p.getMetadata().put("useRest", true);
//        this.getConnector().sendMsgToConnector(p);
//        this.multiThreadedRequestHandler.sendResponse(jingleSDP);
//    }

    @Override
    public void onTransportError(Payload payload) {

    }

    @Override
    public void onTransportStatus(Payload payload) {

    }

}
