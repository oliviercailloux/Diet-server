package io.github.oliviercailloux.sample_quarkus_heroku;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class LocalLogin {
	@Id
	@GeneratedValue
	private int id;

	@Column(nullable = false)
	private String loginId;
	@Column(nullable = false)
	private String password;

	public LocalLogin() {
		loginId = null;
		password = null;
	}

	public LocalLogin(String loginId, String password) {
		this.loginId = checkNotNull(loginId);
		this.password = checkNotNull(password);
	}

	public int getId() {
		return id;
	}

	public String getLoginId() {
		return loginId;
	}

	public String getPassword() {
		return password;
	}
}
