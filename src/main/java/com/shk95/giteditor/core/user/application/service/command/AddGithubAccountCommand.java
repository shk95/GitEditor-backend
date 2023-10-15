package com.shk95.giteditor.core.user.application.service.command;

import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddGithubAccountCommand {

	private UserId userId;
}
