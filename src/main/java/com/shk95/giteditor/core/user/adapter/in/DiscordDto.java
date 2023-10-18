package com.shk95.giteditor.core.user.adapter.in;

import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DiscordDto {

	private UserId userId;
	private String discordId;
}
