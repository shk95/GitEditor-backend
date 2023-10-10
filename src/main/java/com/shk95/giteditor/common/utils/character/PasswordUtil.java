package com.shk95.giteditor.common.utils.character;

import java.security.SecureRandom;

public class PasswordUtil {

	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String DIGITS = "0123456789";
	private static final String SPECIALS = "!@#$%^&*()+";
	private static final int MIN_PASSWORD_LENGTH = 8;

	public static String generate(final int length) {
		if (length < MIN_PASSWORD_LENGTH) {
			return generate(MIN_PASSWORD_LENGTH);
		}
		StringBuilder password = new StringBuilder(length);
		SecureRandom random = new SecureRandom();

		appendCharacters(password, length / 4, random, UPPERCASE, LOWERCASE, DIGITS, SPECIALS);

		while (password.length() < length) {
			password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
		}

		for (int i = 0; i < length; i++) {
			swapCharacters(password, i, random.nextInt(length));
		}
		return password.toString();
	}

	private static void appendCharacters(StringBuilder password, int times, SecureRandom random, String... charPools) {
		for (int i = 0; i < times; i++) {
			for (String charPool : charPools) {
				password.append(charPool.charAt(random.nextInt(charPool.length())));
			}
		}
	}

	private static void swapCharacters(StringBuilder origin, int i, int j) {
		char temp = origin.charAt(i);
		origin.setCharAt(i, origin.charAt(j));
		origin.setCharAt(j, temp);
	}
}
