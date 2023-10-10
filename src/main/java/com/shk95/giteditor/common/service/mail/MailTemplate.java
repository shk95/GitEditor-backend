package com.shk95.giteditor.common.service.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MailTemplate {
	WELCOME("welcome.ftl"),
	NEW_PASSWORD("new-password.ftl"),
	FIND_PASSWORD("find-password.ftl");

	private final String template;
}
