package org.example.chatwebsocketspring.model.dto;

import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document
public record ChatMessage(
        String content,
        String sender,
        MessageType type
) {
}
