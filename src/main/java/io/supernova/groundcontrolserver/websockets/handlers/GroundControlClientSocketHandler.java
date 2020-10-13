package io.supernova.groundcontrolserver.websockets.handlers;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class GroundControlClientSocketHandler extends AbstractWebSocketHandler {
    /**
     * Manage Multiple Ground Control Sessions (e.g. Brenton and I are both testing out comms separately.
     */
    private ArrayList<WebSocketSession> socketSessions;

    public GroundControlClientSocketHandler() {
        this.socketSessions = new ArrayList<WebSocketSession>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.socketSessions.add(session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        boolean didRemove = this.socketSessions.remove(session);
        if (didRemove) {
            System.out.println("Removed Closed Session with id: " , session.getId());
        } else {
            System.out.println("Could not find managed session with id: " + session.getId());
        }
    }

    // TODO: Example of how to communicate to the client without being first prevoked with a message from client.
    private void sendGreetingMessageToClient() throws InterruptedException {
        System.out.println("Session Established!! " + this.socketSessions.toString());
        Thread.sleep(10000);
        this.socketSessions.forEach(clientSession -> {
            try {
                clientSession.sendMessage(new TextMessage("Hello there! Your Session ID is: " + clientSession.getId()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("New Text Message Received: " + message.getPayload());
        session.sendMessage(message);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String convertedMessage = new String(message.getPayload().array(), StandardCharsets.UTF_8);
        System.out.println("New Binary Message Received: " + convertedMessage);
        session.sendMessage(message);
    }
}
