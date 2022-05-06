package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Events are empty in DB iff role is Admin. Events contain at least an Accepted
 * iff role is User, in which case, events start with Accepted in creation
 * order.
 */
@Entity
@UserDefinition
class UserEntity {
	public static final String USER_ROLE = "user";

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserEntity.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonbTransient
	int id;

	@Username
	@NotNull
	@Column(unique = true)
	String username;

	@Password
	@NotNull
	String password;

	@Roles
	@NotNull
	String role;

	protected UserEntity() {
		id = 0;
		username = "should be set";
		password = "should be set";
		role = "should be set";
	}

	UserEntity(String username, String password, String role) {
		this();
		this.username = username;
		checkArgument(!this.username.contains(":"));
		this.password = BcryptUtil.bcryptHash(password);
		this.role = checkNotNull(role);
	}

	String getUsername() {
		return username;
	}

	String role() {
		return role;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("username", username).add("role", role).toString();
	}
}