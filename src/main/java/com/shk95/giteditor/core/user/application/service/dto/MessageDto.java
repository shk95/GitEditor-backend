package com.shk95.giteditor.core.user.application.service.dto;

import com.shk95.giteditor.common.constant.ProviderType;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

public record MessageDto(
	ProviderType senderUserIdProviderType,
	String senderUserIdUserLoginId,
	String senderName,
	ProviderType recipientUserIdProviderType,
	String recipientUserIdUserLoginId,
	String recipientName,
	String content,

	String senderProfileImg,
	String recipientProfileImg,

	@Nullable LocalDateTime timestamp) {

}
