package io.github.oliviercailloux.diet.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Set;
import javax.json.bind.annotation.JsonbPropertyOrder;

/**
 * The status of some user. That user is typically a persistent user; but this
 * object may also represent a fictitious status (not necessarily corresponding
 * to an existing user), e.g., for testing purposes.
 * <p>
 * Immutable
 */
@JsonbPropertyOrder({ "username", "events", "seen", "toSee" })
public class UserStatus {
	private final IUser user;

	private final ImmutableSet<Video> toSee;

	/**
	 * @param user must be persistent; the reference should not be used otherwise
	 *             (to guarantee immutability of the returned object).
	 */
	static UserStatus fromExistingUser(User user, Set<Video> toSee) {
		checkArgument(user.isPersistent());
		return new UserStatus(UserPersistentWithEvents.persistent(user), toSee);
	}

	static UserStatus fromFictitious(String username, Set<ReadEvent> events, Set<Video> toSee) {
		return new UserStatus(UserFictitious.fictitious(username, events), toSee);
	}

	static UserStatus fromIUser(IUser user, Set<Video> toSee) {
		return new UserStatus(user, toSee);
	}

	private UserStatus(IUser user, Set<Video> toSee) {
		this.user = checkNotNull(user);
		this.toSee = ImmutableSet.copyOf(toSee);
	}

	public String getUsername() {
		return user.username();
	}

	public ImmutableSortedSet<ReadEvent> getEvents() {
		return user.readEvents();
	}

	public ImmutableList<Video> getSeen() {
		return user.readSeen();
	}

	public ImmutableSet<Video> getToSee() {
		return toSee;
	}
}
