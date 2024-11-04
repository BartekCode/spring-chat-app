package org.example.chatwebsocketspring.services.chatroom;

import org.example.chatwebsocketspring.model.chatroom.ChatRoom;
import org.example.chatwebsocketspring.services.chatroom.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository repository;

    public ChatRoomServiceImpl(ChatRoomRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<String> getChatRoomId(String senderId, String receiverId, boolean createNewRoomIfNotExists) {
        return repository.findBySenderIdAndReceiverId(senderId, receiverId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                       return Optional.of(createChatId(senderId, receiverId));
                    } else {
                        return Optional.empty();
                    }
                });
    }

    private String createChatId(String senderId, String receiverId) {
        String chatId = String.format("%s-%s", senderId, receiverId);

        ChatRoom senderReceiver = ChatRoom.builder()
                .chatId(chatId)
                .receiverId(receiverId)
                .senderId(senderId)
                .build();

        ChatRoom receiverSender = ChatRoom.builder()
                .chatId(chatId)
                .receiverId(senderId)
                .senderId(receiverId)
                .build();

        repository.save(senderReceiver);
        repository.save(receiverSender);
        return chatId;
    }
}
