package com.shk95.giteditor.domain.application.commands;

import com.shk95.giteditor.domain.model.user.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddGithubAccountCommand {

	private UserId userId;
}
