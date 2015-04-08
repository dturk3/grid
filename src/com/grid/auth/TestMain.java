package com.grid.auth;


public class TestMain {
	public static void main(String[] args) throws Exception {
		final AuthService auth = new AuthService();

		final String token = auth.requestPasswordChange("test@gmail.com");
		auth.authenticate("test@gmail.com", "password");
		auth.changePassword(token, "test");
		auth.authenticate("test@gmail.com", "test");
	}
}
