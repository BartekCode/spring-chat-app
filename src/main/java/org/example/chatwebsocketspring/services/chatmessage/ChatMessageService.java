package org.example.chatwebsocketspring.services.chatmessage;

import org.example.chatwebsocketspring.model.chatmessage.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);
    List<ChatMessage> findChatMessages(String senderId,String receiverId);
}
