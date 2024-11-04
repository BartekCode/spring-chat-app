package org.example.chatwebsocketspring.services.chatroom;

import java.util.Optional;

public interface ChatRoomService {
    Optional<String> getChatRoomId(
            String senderId,
            String receiverId,
            boolean createNewRoomIfNotExists
    );
}
