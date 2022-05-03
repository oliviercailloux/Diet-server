package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.time.Instant;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
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
 * <p>
 * It is permitted to create an event with a null user (required to bootstrap).
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@NotNull
	@JsonbTransient
	UserEntity user;

	@NotNull
	Instant creation;

	protected Event() {
		/* For JPA. */
	}

	protected Event(Instant creation) {
		this.user = null;
		this.creation = checkNotNull(creation);
	}

	@JsonbCreator
	public Event(@JsonbProperty("user") UserEntity user, @JsonbProperty("creation") Instant creation) {
		checkNotNull(user);
		checkNotNull(creation);
		this.user = user;
		this.creation = creation;
	}

	public UserEntity getUser() {
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