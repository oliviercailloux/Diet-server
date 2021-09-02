package io.github.oliviercailloux.diet.dao;

import static com.google.common.base.Preconditions.checkNotNull;

public class Login {
	private final String username;
	private final String password;

	public Login(String username, String password) {
		this.username = checkNotNull(username);
		this.password = checkNotNull(password);
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
