package com.tb.calling;

import lombok.Data;

@Data
public class ICECandidate {
    private String id;
    private String ipAddress;
    private int port;
    private CandidateType candidateType;
    private TransportProtocol transportProtocol;

    public ICECandidate(String id, String ipAddress, int port,
                        CandidateType candidateType, TransportProtocol transportProtocol) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
        this.candidateType = candidateType;
        this.transportProtocol = transportProtocol;
    }
}

