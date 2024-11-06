package org.example.chatwebsocketspring.services.user;

import org.example.chatwebsocketspring.model.user.Status;
import org.example.chatwebsocketspring.model.user.User;
import org.example.chatwebsocketspring.services.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(User user) {
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
        userSavedLog(user);
    }

    @Override
    public void disconnect(User user) {
        User storedUser = userRepository.findById(user.getNickName()).orElse(null);
        if (storedUser != null) {
            storedUser.setStatus(Status.OFFLINE);
            userRepository.save(storedUser);
            userSavedLog(user);
            log.info("User: {}, disconnected.", user.getNickName());
        }
    }

    @Override
    public List<User> findConnectedUsers() {
        List<User> allByStatus = userRepository.findAllByStatus(Status.ONLINE);
        log.info("Found connected users: {}", allByStatus);
        return allByStatus;
    }

    private static void userSavedLog(User user) {
        log.info("User saved: {}", user);
    }
}
