package org.example.chatwebsocketspring.services;

import org.example.chatwebsocketspring.model.entity.User;

import java.util.List;

public interface UserService {
    void save(User user);
    void disconnect(User user);
    List<User> findConnectedUsers();
}
