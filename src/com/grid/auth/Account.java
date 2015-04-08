package com.grid.auth;

import java.io.Serializable;

public class Account implements Serializable {
	private static final long serialVersionUID = 3719826183218817955L;

	private final String mUsername;
	private final String mEmail;
	private String mPassword;
	private String mReset;
	
	public Account(String username, String password, String email) {
		mUsername = username;
		mPassword = password;
		mEmail = email;
		mReset = null;
	}
	
	public String getUsername() {
		return mUsername;
	}
	
	public String getPassword() { 
		return mPassword;
	}
	
	public String getEmail() {
		return mEmail;
	}
	
	public String resetPassword() {
		mReset = Utils.randomReset();
		return mReset;
	}
	
	public void changePassword(String newPassword) {
		if (mReset == null) {
			throw new IllegalStateException();
		}
		mPassword = newPassword;
		mReset = null;
	}
	
	public String getReset() {
		return mReset;
	}
	
	@Override
	public String toString() {
		return getUsername() + " [" + getEmail() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mEmail == null) ? 0 : mEmail.hashCode());
		result = prime * result
				+ ((mUsername == null) ? 0 : mUsername.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (mEmail == null) {
			if (other.mEmail != null)
				return false;
		} else if (!mEmail.equals(other.mEmail))
			return false;
		if (mUsername == null) {
			if (other.mUsername != null)
				return false;
		} else if (!mUsername.equals(other.mUsername))
			return false;
		return true;
	}
	
	
}
