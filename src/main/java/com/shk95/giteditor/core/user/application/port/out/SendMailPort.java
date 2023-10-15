package com.shk95.giteditor.core.user.application.port.out;

public interface SendMailPort {

	void send();

	String sendVerificationEmail(String email);

	String sendRegisterVerificationEmail(String email);
}
