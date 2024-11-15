package com.tb.websocket;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;

import java.nio.ByteBuffer;

public class ListenerEndPoint implements Session.Listener
{
    private Session session;
    @Override
    public void onWebSocketOpen(Session session)
    {
        // The WebSocket endpoint has been opened.

        // Store the session to be able to send data to the remote peer.
        this.session = session;

        // You may configure the session.
        session.setMaxTextMessageSize(16 * 1024);

        // You may immediately send a message to the remote peer.
        session.sendText("connected", Callback.from(session::demand, Throwable::printStackTrace));
    }

    @Override
    public void onWebSocketText(String message)
    {
        // A WebSocket text message is received.

        // You may echo it back if it matches certain criteria.
        if (message.startsWith("echo:"))
        {
            // Only demand for more events when sendText() is completed successfully.
            session.sendText(message.substring("echo:".length()), Callback.from(session::demand, Throwable::printStackTrace));
        }
        else
        {
            // Discard the message, and demand for more events.
            session.demand();
        }
    }

    @Override
    public void onWebSocketBinary(ByteBuffer payload, Callback callback)
    {
        // A WebSocket binary message is received.

        // Save only PNG images.
        boolean isPNG = true;
        byte[] pngBytes = new byte[]{(byte)0x89, 'P', 'N', 'G'};
        for (int i = 0; i < pngBytes.length; ++i)
        {
            if (pngBytes[i] != payload.get(i))
            {
                // Not a PNG image.
                isPNG = false;
                break;
            }
        }

        if (isPNG)
            //savePNGImage(payload);

        // Complete the callback to release the payload ByteBuffer.
        callback.succeed();

        // Demand for more events.
        session.demand();
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        // The WebSocket endpoint failed.

        // You may log the error.
        cause.printStackTrace();

        // You may dispose resources.
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        // The WebSocket endpoint has been closed.

        // You may dispose resources.
    }
}