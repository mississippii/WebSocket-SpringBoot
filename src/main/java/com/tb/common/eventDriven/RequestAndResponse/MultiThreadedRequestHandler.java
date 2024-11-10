package com.tb.common.eventDriven.RequestAndResponse;

import com.tb.transport.Transport;
import lombok.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
public class MultiThreadedRequestHandler {
    private final ExecutorService executorService;
    private Transport transport;

    // Constructor that initializes the ExecutorService with the max number of threads
    public MultiThreadedRequestHandler(Transport transport) {
        int maxThreads = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.transport = transport;
    }

    // Method to handle incoming requests and dispatch them asynchronously
    public void sendResponse(Request request) {
        // Dispatch the request asynchronously by submitting it to the ExecutorService
        executorService.submit(() -> {
            try {
                // Process the request here (this runs in a separate thread)
                processRequest(request);
            } catch (Exception e) {
                // Handle the exception, print stack trace, and continue execution
                System.err.println("Error processing request: " + request);
                e.printStackTrace();
            }
        });
    }

    // Process the request (this method runs in a separate thread)
    private void processRequest(Request request) throws Exception {
        //for now just send response
        Payload response = request.generateResponse();
        this.transport.sendMessage(response);
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
