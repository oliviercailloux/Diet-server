package io.github.oliviercailloux.sample_quarkus_heroku;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import javax.persistence.Entity;

@Entity
public class EventAccepted extends Event {

	public EventAccepted() {
		/* Nothing. */
	}

	@Override
	public String toString() {
		final ToStringHelper stringHelper = MoreObjects.toStringHelper(this);
		populate(stringHelper);
		return stringHelper.toString();
	}

}