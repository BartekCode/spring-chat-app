package org.example.chatwebsocketspring.services.user;

import org.example.chatwebsocketspring.model.user.Status;
import org.example.chatwebsocketspring.model.user.User;
import org.example.chatwebsocketspring.services.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(User user) {
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
    }

    @Override
    public void disconnect(User user) {
        User storedUser = userRepository.findById(user.getNickName()).orElse(null);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            userRepository.save(storedUser);
        }
    }

    @Override
    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }
}