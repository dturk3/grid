package com.grid.auth;

import java.util.Date;

import org.prevayler.TransactionWithQuery;

public class ResetPasswordTransaction implements TransactionWithQuery<AccountDatabase, Account> {
	private static final long serialVersionUID = 6977145361944851469L;

	private final String mEmail;
	
	public ResetPasswordTransaction(String email) {
		mEmail = email;
	}

	@Override
	public Account executeAndQuery(AccountDatabase prevalentSystem,
			Date executionTime) {
		final Account matchedAccount = new QueryAccountTransaction(null, mEmail).query(prevalentSystem, executionTime);
		if (matchedAccount == null) {
			throw new IllegalStateException("No account found for email '" + mEmail + "'.");
		}
		matchedAccount.resetPassword();
		return matchedAccount;
	}
}
