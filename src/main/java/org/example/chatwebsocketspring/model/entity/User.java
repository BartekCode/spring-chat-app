package org.example.chatwebsocketspring.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class User {

    @Id
    private String nickName;
    private String fullName;
    private Status status;
}
