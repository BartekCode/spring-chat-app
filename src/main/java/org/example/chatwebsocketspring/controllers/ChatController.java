package org.example.chatwebsocketspring.controllers;

import org.example.chatwebsocketspring.model.chatmessage.ChatMessage;
import org.example.chatwebsocketspring.model.chatmessage.ChatNotification;
import org.example.chatwebsocketspring.model.dto.ChatPublicMessageDTO;
import org.example.chatwebsocketspring.services.chatmessage.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;

import static org.example.chatwebsocketspring.Constants.CHAT_MESSAGE_DESTINATION;

@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(ChatMessageService chatMessageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatMessageService = chatMessageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat.join") // url to invoke this method
    @SendTo("/topic/public") //to which topic/queue send message
    // endpoint to join the chat and informa other users
    public ChatPublicMessageDTO joinChat(
            @Payload ChatPublicMessageDTO chatPublicMessageDTO,
            SimpMessageHeaderAccessor headerAccessor
            ){
        // add userName in webSocket session
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("userName", chatPublicMessageDTO.sender());
        return chatPublicMessageDTO;
    }

    // wysyłanie wiadomosci na publiczny czat
    @MessageMapping("/chat.send-message") // url to invoke this method
    @SendTo(CHAT_MESSAGE_DESTINATION) //to which topic/queue send message
    public ChatPublicMessageDTO sendMessage(@Payload ChatPublicMessageDTO chatPublicMessageDTO) { //zamist @Requst/Response body tutaj jest @payload
        return chatPublicMessageDTO;
    }

    @MessageMapping("/chat.send-private-message")
    public void processPrivateMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMessage = chatMessageService.save(chatMessage);
        // bartek/queue/private
        simpMessagingTemplate.convertAndSendToUser(
                savedMessage.getRecipientId(),
                "/queue/messages",
                ChatNotification.builder()
                        .id(savedMessage.getId())
                        .recipientId(savedMessage.getRecipientId())
                        .senderId(savedMessage.getSenderId())
                        .content(savedMessage.getContent())
                        .build()
        );
    }

    //odbieramy z metody powyżej
    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable String senderId, @PathVariable String recipientId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

}
