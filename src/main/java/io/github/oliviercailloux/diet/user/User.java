package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class User implements RawUser {
	static RawUser persistent(UserEntity user) {
		return new User(user);
	}

	final UserEntity user;

	private User(UserEntity user) {
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
