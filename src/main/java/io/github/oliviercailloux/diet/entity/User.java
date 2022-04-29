package io.github.oliviercailloux.diet.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.dao.Login;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import java.util.ArrayList;
import java.util.List;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
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
@NamedQuery(name = "getUserWithoutEvents", query = "SELECT u FROM User u WHERE u.username = :username")
@NamedQuery(name = "getUser", query = "SELECT u FROM User u LEFT OUTER JOIN FETCH u.events WHERE u.username = :username")
public class User {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonbTransient
	private int id;

	@Username
	@NotNull
	@Column(unique = true)
	@JsonbTransient
	String username;
	@Password
	@NotNull
	@JsonbTransient
	String password;
	@Roles
	@NotNull
	@JsonbTransient
	String role;

	@OneToMany(mappedBy = "user")
	@NotNull
	List<Event> events;

	public User() {
		events = new ArrayList<>();
	}

	@JsonbCreator
	public User(@JsonbProperty("login") Login login, @JsonbProperty("role") String role) {
		this();
		this.username = login.getUsername();
		this.password = BcryptUtil.bcryptHash(login.getPassword());
		this.role = checkNotNull(role);
	}

	void rectifyEvents() {
		events.forEach(e -> e.user = this);
	}

	public String getUsername() {
		return username;
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
		return MoreObjects.toStringHelper(this).add("id", id).add("username", username).add("role", role).toString();
	}
}