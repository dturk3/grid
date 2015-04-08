package com.grid.auth;

import java.util.Date;

import org.prevayler.Query;

public class QueryAccountByResetTransaction implements Query<AccountDatabase, Account> {
	private static final long serialVersionUID = -1155034237977019671L;
	
	private String mReset;
	
	public QueryAccountByResetTransaction(String reset) {
		mReset = reset;
	}
	
	@Override
	public Account query(AccountDatabase prevalentSystem, Date executionTime)
			throws Exception {
		if (mReset != null) {
			for (Account account : prevalentSystem.getAccounts().values()) {
				if (mReset.equals(account.getReset())) {
					return account;
				}
			}
		}
		return null;
	}

}
