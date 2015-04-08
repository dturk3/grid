package com.grid.auth;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

public class AuthService {
	final Prevayler<AccountDatabase> mDb;
	
	public AuthService() throws Exception {
		mDb = PrevaylerFactory.<AccountDatabase>createPrevayler(new AccountDatabase());
	}
	
	public Account createAccount(String username, String email, String password) {
		final Account account = new Account(username, password, email);
		mDb.execute(new CreateAccountTransaction(account));
		return account;
	}
	
	public void authenticate(String email, String password) {
		mDb.execute(new AuthenticateAccountTransaction(email, password));
	}
	
	public String requestPasswordChange(String email) throws Exception {
		return mDb.execute(new ResetPasswordTransaction(email)).getReset();
	}
	
	public void changePassword(String token, String password) throws Exception {
		mDb.execute(new ChangePasswordTransaction(token, password));
	}
}
