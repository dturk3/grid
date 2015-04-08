package com.grid.auth;

import java.util.Date;

import org.prevayler.TransactionWithQuery;

public class ChangePasswordTransaction implements TransactionWithQuery<AccountDatabase, Account> {
	private static final long serialVersionUID = 1172482700275879741L;

	private final String mReset;
	private final String mPassword;
	
	public ChangePasswordTransaction(String reset, String password) {
		mReset = reset;
		mPassword = password;
	}

	@Override
	public Account executeAndQuery(AccountDatabase prevalentSystem,
			Date executionTime) throws Exception {
		final Account matchedAccount = new QueryAccountByResetTransaction(mReset).query(prevalentSystem, executionTime);
		if (matchedAccount == null) {
			throw new IllegalStateException("Invalid reset token.");
		}
		matchedAccount.changePassword(mPassword);
		return matchedAccount;
	}
}
