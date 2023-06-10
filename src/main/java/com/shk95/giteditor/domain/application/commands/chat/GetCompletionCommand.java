package com.shk95.giteditor.domain.application.commands.chat;

import com.shk95.giteditor.domain.model.user.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetCompletionCommand {

	private UserId userId;
	private String accessToken;
	private int pageAt;
	private int size;
}
