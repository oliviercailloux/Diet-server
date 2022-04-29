package io.github.oliviercailloux.diet.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.dao.Base64String;
import io.github.oliviercailloux.diet.dao.Login;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@UserDefinition
@JsonDeserialize(using = UserDeserializer.class)
@NamedQuery(name = "getBase64UserWithoutEvents", query = "SELECT u FROM User u WHERE u.usernameUtf8ThenBase64Encoded = :username")
@NamedQuery(name = "getBase64User", query = "SELECT u FROM User u LEFT OUTER JOIN FETCH u.events WHERE u.usernameUtf8ThenBase64Encoded = :username")
public class User {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	@Username
	@NotNull
	@Column(unique = true)
	@JsonIgnore
	String usernameUtf8ThenBase64Encoded;
	@Password
	@NotNull
	@JsonIgnore
	String passwordUtf8ThenBase64EncodedThenEncrypted;
	@Roles
	@NotNull
	@JsonIgnore
	String role;

	@OneToMany(mappedBy = "user")
	@NotNull
	List<Event> events;

	public User() {
		events = new ArrayList<>();
	}

	public User(Login login, String role) {
		this();
		this.usernameUtf8ThenBase64Encoded = Base64String.from(login.getUsername()).getRawBase64String();
		LOGGER.debug("Username {} stored as {}.", login.getUsername(), this.usernameUtf8ThenBase64Encoded);
		final String passwordUtf8ThenBase64Encoded = Base64String.from(login.getPassword()).getRawBase64String();
		this.passwordUtf8ThenBase64EncodedThenEncrypted = BcryptUtil.bcryptHash(passwordUtf8ThenBase64Encoded);
		this.role = checkNotNull(role);
	}

	void rectifyEvents() {
		events.forEach(e -> e.user = this);
	}

	public String getUsername() {
		return Base64String.alreadyBase64(usernameUtf8ThenBase64Encoded).getUnencoded();
	}

	public String getRole() {
		return role;
	}

	public ImmutableSet<Event> getEvents() {
		return ImmutableSet.copyOf(events);
	}

	public void addEvent(Event event) {
		checkNotNull(event);
		checkArgument(event.getUser().equals(this));
		final boolean isAccepted = event instanceof EventAccepted;
		checkArgument(isAccepted == events.isEmpty());
		events.add(event);
	}

	/**
	 * @return in order seen
	 */
	public ImmutableSet<Video> getSeen() {
		return events.stream().filter(e -> e instanceof EventSeen).map(e -> (EventSeen) e).map(EventSeen::getVideo)
				.collect(ImmutableSet.toImmutableSet());
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("username base64", usernameUtf8ThenBase64Encoded)
				.add("role", role).toString();
	}
}