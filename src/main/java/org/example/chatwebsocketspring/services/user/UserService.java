package org.example.chatwebsocketspring.services.user;

import org.example.chatwebsocketspring.model.user.User;

import java.util.List;

public interface UserService {
    void save(User user);
    void disconnect(User user);
    List<User> findConnectedUsers();
}
