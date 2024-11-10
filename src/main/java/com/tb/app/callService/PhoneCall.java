package com.tb.app.callService;

public interface PhoneCall {
    void startSession();

    void updateSession();

    void disconnect();

    void onRing();

    void onAnswer();
}
