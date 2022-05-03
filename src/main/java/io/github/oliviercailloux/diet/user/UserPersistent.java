package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSortedSet;
import java.util.Comparator;

class UserPersistent implements RawUser {
	static RawUser persistent(User user) {
		return new UserPersistent(user);
	}

	private final User user;

	private UserPersistent(User user) {
		this.user = checkNotNull(user);
		checkArgument(user.isPersistent());
	}

	@Override
	public String username() {
		return user.getUsername();
	}

	@Override
	public String role() {
		return user.role();
	}

}
