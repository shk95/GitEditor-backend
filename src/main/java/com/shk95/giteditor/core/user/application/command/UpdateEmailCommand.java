package com.shk95.giteditor.core.user.application.command;

import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UpdateEmailCommand {
	private String email;
	private UserId userId;
}
