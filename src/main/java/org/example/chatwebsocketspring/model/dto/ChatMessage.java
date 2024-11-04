package org.example.chatwebsocketspring.model.dto;

import lombok.Builder;

@Builder
public record ChatMessage(
        String content,
        String sender,
        MessageType type
) {
}
