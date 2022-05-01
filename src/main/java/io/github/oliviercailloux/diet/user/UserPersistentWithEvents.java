package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSortedSet;
import java.util.Comparator;

class UserPersistentWithEvents implements IUser {
	static IUser persistent(User user) {
		return new UserPersistentWithEvents(user);
	}

	private final User user;

	private UserPersistentWithEvents(User user) {
		this.user = checkNotNull(user);
		checkArgument(user.isPersistent());
		checkArgument(user.hasEvents());
	}

	@Override
	public String username() {
		return user.getUsername();
	}

	@Override
	public ImmutableSortedSet<ReadEvent> readEvents() {
		return user.events().stream().map(ReadEvent::fromEvent)
				.collect(ImmutableSortedSet.toImmutableSortedSet(Comparator.comparing(ReadEvent::creation)));
	}
}
