package com.grid.auth;

import java.util.Date;

import org.prevayler.Query;

public class QueryAccountTransaction implements Query<AccountDatabase, Account> {
	private static final long serialVersionUID = -1155034237977019671L;
	
	private String mUsername;
	private String mEmail;
	
	public QueryAccountTransaction(String username, String email) {
		mUsername = username;
		mEmail = email;
	}
	
	@Override
	public Account query(AccountDatabase prevalentSystem, Date executionTime) {
		if (mUsername != null) {
			return prevalentSystem.getAccounts().get(mUsername);
		}
		if (mEmail != null) {
			for (Account account : prevalentSystem.getAccounts().values()) {
				if (account.getEmail().equalsIgnoreCase(mEmail)) {
					return account;
				}
			}
		}
		return null;
	}

}
