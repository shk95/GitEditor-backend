package com.shk95.giteditor.common.service.mail;

public interface MailManager {

	/**
	 * Send a message.http to a recipient
	 *
	 * @param emailAddress the recipient's email address
	 * @param subject      the subject key of the email
	 * @param template     the template file name of the email
	 * @param variables    message.http variables in the template file
	 */
	void send(String emailAddress, String subject, MailTemplate template, MessageVariable... variables);
}
