package io.github.oliviercailloux.sample_quarkus_heroku.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Deserializing with the default Jackson strategy will create an event with a
 * null user, which we do not want. We should deserialize only in the context of
 * a User.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@NotNull
	@JsonIgnore
	User user;

	@NotNull
	Instant creation;

	Event() {
		/* For JPA. */
	}

	public Event(User user, Instant creation) {
		checkNotNull(user);
		checkNotNull(creation);
		this.user = user;
		this.creation = creation;
	}

	public User getUser() {
		return user;
	}

	public Instant getCreation() {
		return creation;
	}

	protected void populate(ToStringHelper stringHelper) {
		stringHelper.add("user", user).add("creation", creation);
	}

	@Override
	public String toString() {
		final ToStringHelper stringHelper = MoreObjects.toStringHelper(this);
		populate(stringHelper);
		return stringHelper.toString();
	}

}