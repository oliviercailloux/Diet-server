package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Verify.verify;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSortedSet;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@NamedQuery(name = "getUserWithoutEvents", query = "SELECT u FROM User u WHERE u.username = :username")
@NamedQuery(name = "getUserWithEvents", query = "SELECT u FROM User u LEFT OUTER JOIN FETCH u.events WHERE u.username = :username")
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
	private String username;

	@Password
	@NotNull
	private String password;

	@Roles
	@NotNull
	private String role;

	@OneToMany(mappedBy = "user")
	@NotNull
	@OrderBy("creation")
	private Set<Event> events;

	protected User() {
		id = 0;
		username = "should be set";
		password = "should be set";
		role = "should be set";
		events = null;
	}

	User(Login login, String role) {
		this();
		this.username = login.getUsername();
		this.password = BcryptUtil.bcryptHash(login.getPassword());
		this.role = checkNotNull(role);
		this.events = new LinkedHashSet<>();
	}

	boolean isPersistent() {
		return id != 0;
	}

	String getUsername() {
		return username;
	}

	/**
	 * Returns {@code true} iff this user has at least one event, equivalently, iff
	 * its events have been initialized.
	 *
	 * @return {@code false} iff this user has no events, meaning, its events have
	 *         not been initialized.
	 */
	boolean hasEvents() {
		if (events != null) {
			verify(!events.isEmpty());
		}
		return events != null;
	}

	/**
	 * Returns the events, if {@link #hasEvents()}.
	 *
	 * @return the events, ordered by creation
	 * @throws IllegalStateException iff {@link #hasEvents()} is {@code false}
	 */
	private ImmutableSortedSet<Event> readEvents() {
		checkState(hasEvents());
		return ImmutableSortedSet.copyOf(Comparator.comparing(Event::getCreation), events);
	}

	/**
	 * Returns the events for writing, not necessarily ordered, if this user is
	 * persistent and has events.
	 *
	 * @return the events
	 * @throws IllegalStateException iff this instance is not persistent or
	 *                               {@link #hasEvents()} is {@code false}
	 */
	Set<Event> events() {
		checkState(isPersistent());
		checkState(hasEvents());
		return events;
	}

	private void rectifyEvents() {
		events.forEach(e -> e.user = this);
	}

	private String getRole() {
		return role;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("username", username).add("role", role)
				.add("nb events", events == null ? 0 : events.size()).toString();
	}
}