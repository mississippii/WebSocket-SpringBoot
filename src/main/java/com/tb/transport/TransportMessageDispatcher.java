package com.tb.transport;

import com.tb.common.RawPayload;
import com.tb.common.eventDriven.TransportListener;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class TransportMessageDispatcher {
    private final ExecutorService executorService;
    private List<TransportListener> transportListeners;
    public TransportMessageDispatcher(List<TransportListener> transportListeners) {
        int maxThreads=1;//1 is enough to just dispatch request without much processing
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.transportListeners =transportListeners;
    }
    private void submitToExecutor(RawPayload payload, Consumer<RawPayload> transportListenerCallBack)
    {
        executorService.submit(() -> {
            try {
                transportListenerCallBack.accept(payload);
            } catch (Exception e) {
                System.err.println("Error dispatching message to transport listener: " + payload.getData());
                e.printStackTrace();
            }
        });
    }
}
