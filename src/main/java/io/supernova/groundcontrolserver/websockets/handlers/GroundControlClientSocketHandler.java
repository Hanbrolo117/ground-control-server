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
    private ArrayList<WebSocketSession> socketSessions;

    // TODO: maybe manage a way so that clients can choose which connected agent they'd like to speak with, and prevent others
    // TODO: from connecting to that agent if another client is already connected to it. Also even have ability to request access
    // TODO: from another connected GC client or even allow for Ground to Ground comms (i.e. chat between to GC clients.)
    private ArrayList<WebSocketSession> groundControlClients;
    private ArrayList<WebSocketSession> agents;



    public GroundControlClientSocketHandler() {
        this.socketSessions = new ArrayList<WebSocketSession>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.socketSessions.add(session);

        // TODO: make initial handshake with client and identify their type, i.e. Ground Control, or an Agent.

        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        boolean didRemove = this.socketSessions.remove(session);
        if (didRemove) {
            System.out.println("Removed Closed Session with id: " + session.getId());
        } else {
            System.out.println("Could not find managed session with id: " + session.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // If not assigned a client type
        if (this.socketSessions.contains(session)) {
            // Check if client is asserting it's type.
            boolean isAssigned = false;
            if (message.equals("gcc")) {
                this.groundControlClients.add(session);
                isAssigned = true;
                // TODO: sent message with list of available agents to "pair" with.
            } else if (message.equals("agent")) {
                this.agents.add(session);
                isAssigned = true;
                // TODO: notify all GCC of new connected agent that is available for "pairing".
            }
            if (isAssigned) this.socketSessions.remove(session);
        } else if (this.groundControlClients.contains(session)) {
            // TODO: Handle GCC message
            handleGroundControlClient(session);
        } else if (this.agents.contains(session)) {
            // TODO: Handle Agent message
            handleAgentClient(session);
        }


        System.out.println("New Text Message Received: " + message);
        session.sendMessage(message);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String convertedMessage = new String(message.getPayload().array(), StandardCharsets.UTF_8);
        System.out.println("New Binary Message Received: " + convertedMessage);
        session.sendMessage(message);
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

    private void handleGroundControlClient(WebSocketSession clientSession) {}

    private void handleAgentClient(WebSocketSession agentSession) {}
}
