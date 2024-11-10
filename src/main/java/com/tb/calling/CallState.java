package com.tb.calling;
public enum CallState {
    IDLE,
    SESSION_START,
    CALLER_SDP_RECEIVED,
    WAITING_RINGING,
    WAITING_ANSWER,
    TRYING,
    RINGING,
    ANSWERED,
    HANGUP
}
