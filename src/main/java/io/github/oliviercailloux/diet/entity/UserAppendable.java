package io.github.oliviercailloux.diet.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.diet.VideoService;
import javax.persistence.EntityManager;

public class UserAppendable {

	static UserAppendable fromExistingWithEvents(EntityManager em, User user) {
		return new UserAppendable(em, user);
	}

	private final EntityManager em;
	private final User user;

	private UserAppendable(EntityManager em, User user) {
		this.em = checkNotNull(em);
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

	public UserStatus status(VideoService v) {
		return UserStatus.fromExistingUser(user);
	}
}
