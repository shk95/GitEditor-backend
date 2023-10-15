package com.shk95.giteditor.core.user.application.service.dto;

public record UserDto(
	String userId, // providerType + , + loginId (pk)
	String profileImageUrl,
	String defaultEmail
) {

}
