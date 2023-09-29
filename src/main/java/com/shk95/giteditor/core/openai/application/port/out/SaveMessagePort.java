package com.shk95.giteditor.core.openai.application.port.out;

import com.shk95.giteditor.core.openai.domain.ChatDocument;

public interface SaveMessagePort {

	ChatDocument save(String userId, String prompt, String completion);
}
