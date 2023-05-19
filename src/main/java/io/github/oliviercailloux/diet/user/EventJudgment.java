package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verify;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Entity
class EventJudgment extends Event {

	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private Judgment judgment;

	EventJudgment() {
		/* For JPA. */
	}

	EventJudgment(Instant creation, Judgment judgment) {
		super(creation);
		this.judgment = checkNotNull(judgment);
	}

	EventJudgment(UserEntity user, Instant creation, Judgment judgment) {
		super(user, creation);
		this.judgment = checkNotNull(judgment);
	}

	public Judgment getJudgment() {
		verify(judgment != null);
		return judgment;
	}

	@Override
	public String toString() {
		final ToStringHelper stringHelper = MoreObjects.toStringHelper(this);
		populate(stringHelper);
		return stringHelper.add("Judgment", judgment).toString();
	}

}