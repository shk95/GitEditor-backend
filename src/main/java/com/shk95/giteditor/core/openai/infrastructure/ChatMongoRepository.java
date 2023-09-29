package com.shk95.giteditor.core.openai.infrastructure;

import com.shk95.giteditor.core.openai.domain.ChatDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMongoRepository extends MongoRepository<ChatDocument, String> {

	Page<ChatDocument> findAllByUserIdOrderByCreatedDateDesc(String userId, Pageable pageable);
}
