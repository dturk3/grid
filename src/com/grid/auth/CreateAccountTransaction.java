package com.grid.auth;

import java.util.Date;

import org.prevayler.Transaction;

public class CreateAccountTransaction implements Transaction<AccountDatabase> {
	private static final long serialVersionUID = -7169927290880783332L;

	private final Account mAccount;
	
	public CreateAccountTransaction(Account account) {
		mAccount = account;
	}
	
	@Override
	public void executeOn(AccountDatabase prevalentSystem, Date executionTime) {
		if (prevalentSystem.getAccounts().containsKey(mAccount.getUsername())) {
			throw new IllegalStateException("Username '" + mAccount.getUsername() + "' already exists.");
		}
		Account matchedByEmail = null;
		try {
			matchedByEmail  = new QueryAccountTransaction(null, mAccount.getEmail()).query(prevalentSystem, executionTime);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to query accounts by email.");
		}
		if (matchedByEmail != null) {
			throw new IllegalStateException("Email '" + mAccount.getEmail() + "' already has an account.");
		}
		prevalentSystem.getAccounts().put(mAccount.getUsername(), mAccount);
	}
}
