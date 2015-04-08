package com.grid.auth;

import java.util.Date;

import org.prevayler.Transaction;

public class AuthenticateAccountTransaction implements Transaction<AccountDatabase> {
	private static final long serialVersionUID = 6994437890693671052L;
	
	private final String mEmail;
	private final String mPassword;
	
	public AuthenticateAccountTransaction(String email, String password) {
		mEmail = email;
		mPassword = password;
	}
	
	@Override
	public void executeOn(AccountDatabase prevalentSystem, Date executionTime) {
		Account matchedByEmail = null;
		try {
			matchedByEmail  = new QueryAccountTransaction(null, mEmail).query(prevalentSystem, executionTime);
		} catch (Exception e) {
			throw new IllegalStateException("Error when logging in.");
		}
		if (matchedByEmail == null) {
			throw new IllegalStateException("Email or password incorrect.");
		}
		if (!matchedByEmail.getPassword().equals(mPassword)) {
			throw new IllegalStateException("Email or password incorrect.");
		}
	}
}
