package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import java.util.Objects;
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

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof Login)) {
			return false;
		}
		final Login t2 = (Login) o2;
		return username.equals(t2.username) && password.equals(t2.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, password);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("username", username).toString();
	}
}
