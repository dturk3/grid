package com.grid.auth;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static String randomReset() {
		return String.valueOf(UUID.randomUUID());
	}

	public static boolean isValidEmail(final String email) {
		final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		final Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
