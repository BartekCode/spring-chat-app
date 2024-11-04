package org.example.chatwebsocketspring.controllers;

import org.example.chatwebsocketspring.model.dto.ChatMessage;
import org.example.chatwebsocketspring.model.user.User;
import org.example.chatwebsocketspring.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.example.chatwebsocketspring.Constants.CHAT_MESSAGE_DESTINATION;

@Controller
public class ChatController {

    private final UserService userService;

    public ChatController(UserService userService) {
        this.userService = userService;
    }

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

    // wysyłanie wiadomosci na publiczny czat
    @MessageMapping("/chat.send-message") // url to invoke this method
    @SendTo(CHAT_MESSAGE_DESTINATION) //to which topic/queue send message
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage ) { //zamist @Requst/Response body tutaj jest @payload
        return chatMessage;
    }

    // wysyłanie wiadomosci prywatnej
    @MessageMapping("/chat.send-private-message")
    @SendTo(CHAT_MESSAGE_DESTINATION)
    public ChatMessage sendPrivateMessage(@Payload ChatMessage chatMessage ) { //zamist @Requst/Response body tutaj jest @payload
        return chatMessage;
    }

    // user related controllers
    @MessageMapping("/user.add-user")
    @SendTo("/user/topic")
    public User addUser(
            @Payload User user
    ) {
        userService.save(user);
        return user;
    }

    @MessageMapping("/user.disconnect-user")
    @SendTo("/user/topic")
    public User disconnectUser(
            @Payload User user
    ) {
        userService.save(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getConnectedUsers() {
        return ResponseEntity.of(Optional.ofNullable(userService.findConnectedUsers()));
    }
}
