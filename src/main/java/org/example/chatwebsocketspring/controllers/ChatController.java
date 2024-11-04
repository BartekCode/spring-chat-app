package org.example.chatwebsocketspring.controllers;

import org.example.chatwebsocketspring.model.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Objects;

import static org.example.chatwebsocketspring.Constants.CHAT_MESSAGE_DESTINATION;

@Controller
public class ChatController {

    @MessageMapping("/chat.join") // url to invoke this method
    @SendTo("/topic/public") //to which topic/queue send message
    // endpoint to join the chat and informa other users
    public ChatMessage joinChat(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
            ){
        // add userName in webSocket session
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("userName", chatMessage.sender());
        return chatMessage;
    }

    @MessageMapping("/chat.send-message") // url to invoke this method
    @SendTo(CHAT_MESSAGE_DESTINATION) //to which topic/queue send message
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage ) { //zamist @Requst/Response body tutaj jest @payload
        return chatMessage;
    }
}
