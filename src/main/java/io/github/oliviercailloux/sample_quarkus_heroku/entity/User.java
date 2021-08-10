package io.github.oliviercailloux.sample_quarkus_heroku.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
@UserDefinition
@JsonDeserialize(using = UserDeserializer.class)
@NamedQuery(name = "getUser", query = "SELECT u FROM User u LEFT OUTER JOIN FETCH u.events WHERE username = :username")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	@Username
	@NotNull
	@Column(unique = true)
	private String username;
	@Password
	@NotNull
	@JsonIgnore
	private String password;
	@Roles
	@NotNull
	@JsonIgnore
	private String role;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	@NotNull
	List<Event> events;

	public User() {
		events = new ArrayList<>();
	}

	public User(String username, String encryptedPassword, String role) {
		this();
		this.username = checkNotNull(username);
		this.password = checkNotNull(encryptedPassword);
		this.role = checkNotNull(role);
	}

	void rectifyEvents() {
		events.forEach(e -> e.user = this);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		checkArgument(!role.contains(","));
		this.role = role;
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