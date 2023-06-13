package com.shk95.giteditor.domain.model.chat;

import com.shk95.giteditor.domain.model.user.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String> {

	Page<Chat> findAllByUserIdOrderByCreatedDateDesc(UserId userId, Pageable pageable);
}
