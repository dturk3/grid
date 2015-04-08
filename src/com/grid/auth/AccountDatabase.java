package com.grid.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AccountDatabase implements Serializable {
	private static final long serialVersionUID = 2669475478317871794L;

	private Map<String,Account> mAccounts = new HashMap<String,Account>();
	
	public Map<String,Account> getAccounts() {
		return mAccounts;
	}
	
	public void setAccounts(Map<String,Account> accounts) {
		mAccounts = accounts;
	}
}
