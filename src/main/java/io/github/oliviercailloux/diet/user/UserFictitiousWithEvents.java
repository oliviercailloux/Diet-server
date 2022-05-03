package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSortedSet;
import java.util.Set;

class UserFictitiousWithEvents implements IUser {

	static IUser fictitious(String username, Set<ReadEvent> events) {
		return new UserFictitiousWithEvents(username, events);
	}

	private final String username;
	private final ImmutableSortedSet<ReadEvent> events;

	private UserFictitiousWithEvents(String username, Set<ReadEvent> events) {
		this.username = checkNotNull(username);
		this.events = ImmutableSortedSet.copyOf(ReadEvent.COMPARATOR, events);
	}

	@Override
	public String username() {
		return username;
	}

	@Override
	public String role() {
		return User.USER_ROLE;
	}

	@Override
	public ImmutableSortedSet<ReadEvent> readEvents() {
		return events;
	}
}
