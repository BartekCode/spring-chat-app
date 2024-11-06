package org.example.chatwebsocketspring.model.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class User {
    @Id
    private String nickName;
    private Status status;
}
