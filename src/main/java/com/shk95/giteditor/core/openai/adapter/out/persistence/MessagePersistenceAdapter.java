package com.shk95.giteditor.core.openai.adapter.out.persistence;

import com.shk95.giteditor.core.openai.application.port.out.LoadMessagePort;
import com.shk95.giteditor.core.openai.application.port.out.SaveMessagePort;
import com.shk95.giteditor.core.openai.application.service.PageInfo;
import com.shk95.giteditor.core.openai.application.service.PageResult;
import com.shk95.giteditor.core.openai.application.service.SortInfo;
import com.shk95.giteditor.core.openai.domain.ChatDocument;
import com.shk95.giteditor.core.openai.infrastructure.ChatMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class MessagePersistenceAdapter implements LoadMessagePort, SaveMessagePort {

	private final ChatMongoRepository chatMongoRepository;

	@Transactional(readOnly = true)
	@Override
	public PageResult<ChatDocument> findAllByUserIdOrderByCreatedDateDesc(String userId, int pageAt, int size, SortInfo sortInfo) {
		PageRequest pageRequest = PageRequest.of(
			pageAt,
			size,
			Sort.by(Sort.Direction.fromString(sortInfo.getDirection().name()), sortInfo.getProperty())
		);
		Page<ChatDocument> pageResult = chatMongoRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageRequest);
		return PageResult.<ChatDocument>builder()
			.items(pageResult.getContent().stream()
				.map(item -> ChatDocument.builder()
					.completion(item.getCompletion())
					.prompt(item.getPrompt())
					.createdDate(item.getCreatedDate()).build())
				.collect(Collectors.toList()))
			.pageInfo(PageInfo.builder()
				.isLast(pageResult.isLast())
				.totalPage(pageResult.getTotalPages())
				.totalSize(pageResult.getSize()).build())
			.build();
	}

	@Transactional
	@Override
	public ChatDocument save(String userId, String prompt, String completion) {
		return chatMongoRepository.save(ChatDocument.builder()
			.userId(userId)
			.prompt(prompt)
			.completion(completion).build());
	}
}
