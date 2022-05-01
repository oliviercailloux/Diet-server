package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.diet.video.VideoFactory;
import javax.persistence.EntityManager;

public class UserAppendable {

	static UserAppendable fromExistingWithEvents(EntityManager em, VideoFactory v, User user) {
		return new UserAppendable(em, v, user);
	}

	private final EntityManager em;
	private final VideoFactory v;
	private final User user;

	private UserAppendable(EntityManager em, VideoFactory v, User user) {
		this.em = checkNotNull(em);
		this.v = checkNotNull(v);
		this.user = checkNotNull(user);
		checkArgument(user.isPersistent());
		checkArgument(user.hasEvents());
	}

	public void persistEvent(ReadEvent event) {
		checkNotNull(event);
		checkArgument(!event.isAccepted());
		final Event underlyingEvent = event.underlyingEvent();
		underlyingEvent.user = user;
		user.events().add(underlyingEvent);
		em.persist(event);
	}

	public UserStatus status() {
		return UserStatus.fromExistingUser(user, v);
	}
}
