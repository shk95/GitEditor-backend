package com.shk95.giteditor.domain.application.commands;

import com.shk95.giteditor.domain.model.user.UserId;
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
