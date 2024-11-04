package org.example.chatwebsocketspring.services.chatmessage.repository;

import org.example.chatwebsocketspring.model.chatmessage.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findAllByChatId(String chatId);
}
