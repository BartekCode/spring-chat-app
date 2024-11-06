package org.example.chatwebsocketspring.controllers;

import org.example.chatwebsocketspring.model.user.User;
import org.example.chatwebsocketspring.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @MessageMapping("/user.add-user")
    @SendTo("/topic/user")
    public User addUser(
            @Payload User user
    ) {
        userService.save(user);
        return user;
    }

    @MessageMapping("/user.disconnect-user")
    @SendTo("/topic/user")
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
