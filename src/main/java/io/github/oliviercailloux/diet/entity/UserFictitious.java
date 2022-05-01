package io.github.oliviercailloux.diet.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSortedSet;
import java.util.Comparator;
import java.util.Set;

class UserFictitious implements IUser {

	static IUser fictitious(String username, Set<ReadEvent> events) {
		return new UserFictitious(username, events);
	}

	private final String username;
	private final ImmutableSortedSet<ReadEvent> events;

	private UserFictitious(String username, Set<ReadEvent> events) {
		this.username = checkNotNull(username);
		this.events = ImmutableSortedSet.copyOf(Comparator.comparing(ReadEvent::creation), events);
	}

	@Override
	public String username() {
		return username;
	}

	@Override
	public ImmutableSortedSet<ReadEvent> readEvents() {
		return events;
	}
}
