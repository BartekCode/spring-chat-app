package org.example.chatwebsocketspring.services.repository;

import org.example.chatwebsocketspring.model.entity.Status;
import org.example.chatwebsocketspring.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// tworzymy takie mongowe spring data jpa repo
public interface UserRepository extends MongoRepository<User, String> {
    // metoda stworzona na nasze potrzeby
    List<User> findAllByStatus(Status status);
}

