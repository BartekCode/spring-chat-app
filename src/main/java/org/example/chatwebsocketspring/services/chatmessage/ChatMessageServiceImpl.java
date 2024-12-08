package org.example.chatwebsocketspring.services.chatmessage;

import org.example.chatwebsocketspring.model.chatmessage.ChatMessage;
import org.example.chatwebsocketspring.services.chatmessage.repository.ChatMessageRepository;
import org.example.chatwebsocketspring.services.chatroom.ChatRoomService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository, ChatRoomService chatRoomService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomService = chatRoomService;
    }

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getReceiverId(), true)
                .orElseThrow(); // TODO create exception
        chatMessage.setChatId(chatId);
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> findChatMessages(String senderId, String receiverId) {
        var chatId = chatRoomService.getChatRoomId(senderId, receiverId, false);
        return chatId.map(chatMessageRepository::findAllByChatId).orElse(List.of());
    }
}
