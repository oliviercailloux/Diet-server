package io.github.oliviercailloux.diet.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class Login {
	private final String username;
	private final String password;

	@JsonbCreator
	public Login(@JsonbProperty("username") String username, @JsonbProperty("password") String password) {
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
