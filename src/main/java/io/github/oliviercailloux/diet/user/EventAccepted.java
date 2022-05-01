package io.github.oliviercailloux.diet.user;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.time.Instant;
import javax.persistence.Entity;

@Entity
public class EventAccepted extends Event {

	EventAccepted() {
		/* For JPA. */
	}

	public EventAccepted(Instant creation) {
		super(creation);
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