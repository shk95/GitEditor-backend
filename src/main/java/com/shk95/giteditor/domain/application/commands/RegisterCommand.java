package com.shk95.giteditor.domain.application.commands;

import org.springframework.util.Assert;

import java.util.Objects;

@Deprecated
public class RegisterCommand extends AnonymousCommand {

	private final String username;
	private final String emailAddress;
	private final String password;

	public RegisterCommand(String username, String emailAddress, String password) {
		Assert.hasText(username, "Parameter `username` must not be empty");
		Assert.hasText(emailAddress, "Parameter `emailAddress` must not be empty");
		Assert.hasText(password, "Parameter `password` must not be empty");

		this.username = username;
		this.emailAddress = emailAddress;
		this.password = password;
	}

	public String getUsername() {
		return this.username;
	}

	public String getEmailAddress() {
		return this.emailAddress;
	}

	public String getPassword() {
		return this.password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RegisterCommand)) return false;
		RegisterCommand that = (RegisterCommand) o;
		return Objects.equals(username, that.username) &&
			Objects.equals(emailAddress, that.emailAddress) &&
			Objects.equals(password, that.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, emailAddress, password);
	}

	@Override
	public String toString() {
		return "RegisterCommand{" +
			"username='" + username + '\'' +
			", emailAddress='" + emailAddress + '\'' +
			", password='" + password + '\'' +
			'}';
	}
}
