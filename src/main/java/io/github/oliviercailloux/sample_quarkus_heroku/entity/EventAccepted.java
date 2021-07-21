package io.github.oliviercailloux.sample_quarkus_heroku.entity;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.time.Instant;
import javax.persistence.Entity;

@Entity
public class EventAccepted extends Event {

	EventAccepted() {
		/* For JPA. */
	}

	public EventAccepted(User user, Instant creation) {
		super(user, creation);
	}

	@Override
	public String toString() {
		final ToStringHelper stringHelper = MoreObjects.toStringHelper(this);
		populate(stringHelper);
		return stringHelper.toString();
	}

}