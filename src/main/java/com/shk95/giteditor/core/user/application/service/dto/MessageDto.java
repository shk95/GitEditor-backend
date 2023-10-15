package com.shk95.giteditor.core.user.application.service.dto;

import com.shk95.giteditor.common.constant.ProviderType;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

public record MessageDto(
	ProviderType senderUserIdProviderType,
	String senderUserIdUserLoginId,
	ProviderType recipientUserIdProviderType,
	String recipientUserIdUserLoginId,
	String content,
	@Nullable LocalDateTime timestamp) {

}
