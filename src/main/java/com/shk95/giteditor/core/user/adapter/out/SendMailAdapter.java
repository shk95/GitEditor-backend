package com.shk95.giteditor.core.user.adapter.out;

import com.shk95.giteditor.common.service.mail.MailManager;
import com.shk95.giteditor.common.service.mail.MailTemplate;
import com.shk95.giteditor.common.service.mail.MessageVariable;
import com.shk95.giteditor.config.ApplicationProperties;
import com.shk95.giteditor.core.user.application.port.out.SendMailPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendMailAdapter implements SendMailPort {

	private final MailManager mailManager;
	private final ApplicationProperties properties;

	public void send() {

	}

	public String sendVerificationEmail(String email) {
		String code = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
		String home = properties.getFrontPageUrl();
		mailManager.send(
			email,
			"Welcome to GitEditor",
			MailTemplate.WELCOME,
			MessageVariable.from("code", home + "/redirect?type=emailVerification&code=" + code),
			MessageVariable.from("message", "회원가입을 축하합니다. 아래의 링크를 클릭해서 계정을 활성화해 주세요.")
		);
		return code;
	}

	public String sendRegisterVerificationEmail(String email) {
		String code = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
		String home = properties.getFrontPageUrl();
		mailManager.send(
			email,
			"이메일 변경 안내",
			MailTemplate.WELCOME,
			MessageVariable.from("code", home + "/redirect?type=emailVerification&code=" + code),
			MessageVariable.from("message", "아래의 링크를 클릭해서 이메일을 확인해 주세요.")
		);
		return code;
	}
}
