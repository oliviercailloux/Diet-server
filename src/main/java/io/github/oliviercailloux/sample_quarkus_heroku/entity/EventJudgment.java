package io.github.oliviercailloux.sample_quarkus_heroku.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import java.time.Instant;
import javax.persistence.Entity;

@Entity
public class EventJudgment extends Event {

	private Judgment judgment;

	EventJudgment() {
		/* For JPA. */
	}

	public EventJudgment(User user, Instant creation, Judgment judgment) {
		super(user, creation);
		this.judgment = checkNotNull(judgment);
	}

	public Judgment getJudgment() {
		return judgment;
	}

	@Override
	public String toString() {
		final ToStringHelper stringHelper = MoreObjects.toStringHelper(this);
		populate(stringHelper);
		return stringHelper.add("Judgment", judgment).toString();
	}

}