package com.shk95.giteditor.common.service.mail.impl;

import com.shk95.giteditor.common.service.mail.*;
import com.shk95.giteditor.config.ApplicationProperties;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class DefaultMailManager implements MailManager {

	private final String mailFrom;
	private final Mailer mailer;
	private final Configuration configuration;

	public DefaultMailManager(ApplicationProperties properties,
	                          Mailer mailer,
	                          Configuration configuration) {
		this.mailFrom = properties.getMailFrom();
		this.mailer = mailer;
		this.configuration = configuration;
	}

	@Override
	public void send(String emailAddress, String subject, MailTemplate template, MessageVariable... variables) {
		Assert.hasText(emailAddress, "Parameter `emailAddress` must not be blank");
		Assert.hasText(subject, "Parameter `subject` must not be blank");
		Assert.hasText(template.getTemplate(), "Parameter `template` must not be blank");

		String messageBody = createMessageBody(template.getTemplate(), variables);
		Message message = new SimpleMessage(emailAddress, subject, messageBody, mailFrom);
		log.info("Email has been sent. Subject : [{}], EmailAddress : [{}], Template : [{}]", subject, emailAddress, template);
		mailer.send(message);
	}

	private String createMessageBody(String templateName, MessageVariable... variables) {
		try {
			Template template = configuration.getTemplate(templateName);
			Map<String, Object> model = new HashMap<>();
			if (variables != null) {
				for (MessageVariable variable : variables) {
					model.put(variable.getKey(), variable.getValue());
				}
			}
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		} catch (Exception e) {
			log.error("Failed to create message.http body from template `" + templateName + "`", e);
			return null;
		}
	}
}
