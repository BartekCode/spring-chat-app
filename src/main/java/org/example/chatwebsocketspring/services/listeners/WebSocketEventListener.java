package org.example.chatwebsocketspring.services.listeners;

import org.example.chatwebsocketspring.model.dto.ChatMessage;
import org.example.chatwebsocketspring.model.dto.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

import static org.example.chatwebsocketspring.Constants.CHAT_MESSAGE_DESTINATION;

@Service
public record WebSocketEventListener(
        SimpMessageSendingOperations messageTemplate
) {

    private static final  Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        // pobieramy headerAccessor z eventu
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userName = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("userName");
        if (userName != null) {
            log.info("User {} disconnected from the chat.", userName);
            ChatMessage userLeftTheChatMsg = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(userName)
                    .build();
            messageTemplate.convertAndSend(CHAT_MESSAGE_DESTINATION, userLeftTheChatMsg); // "/topic/public/"
        }
    }
}