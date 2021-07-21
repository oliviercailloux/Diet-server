package io.github.oliviercailloux.sample_quarkus_heroku;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonDeserialize(using = EventDeserializer.class)
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