package com.shk95.giteditor.core.openai.application.port.out;

import com.shk95.giteditor.core.openai.application.service.PageResult;
import com.shk95.giteditor.core.openai.application.service.SortInfo;
import com.shk95.giteditor.core.openai.domain.ChatDocument;

public interface LoadMessagePort {

	PageResult<ChatDocument> findAllByUserIdOrderByCreatedDateDesc(String userId, int pageAt, int size, SortInfo sortInfo);
}
