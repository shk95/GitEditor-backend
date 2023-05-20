package com.shk95.giteditor.domain.common.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DefaultMailManager implements MailManager {

	private String mailFrom;
	private Mailer mailer;
	private Configuration configuration;

	public DefaultMailManager(@Value("${app.mail-from}") String mailFrom,
							  Mailer mailer,
							  Configuration configuration) {
		this.mailFrom = mailFrom;
		this.mailer = mailer;
		this.configuration = configuration;
	}

	@Override
	public void send(String emailAddress, String subject, String template, MessageVariable... variables) {
		Assert.hasText(emailAddress, "Parameter `emailAddress` must not be blank");
		Assert.hasText(subject, "Parameter `subject` must not be blank");
		Assert.hasText(template, "Parameter `template` must not be blank");

		String messageBody = createMessageBody(template, variables);
		Message message = new SimpleMessage(emailAddress, subject, messageBody, mailFrom);
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
			log.error("Failed to create message body from template `" + templateName + "`", e);
			return null;
		}
	}

}
