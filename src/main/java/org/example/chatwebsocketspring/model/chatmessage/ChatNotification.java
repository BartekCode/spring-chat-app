package org.example.chatwebsocketspring.model.chatmessage;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatNotification {
    private String id;
    private String senderId;
    private String recipientId;
    private String content;
}
