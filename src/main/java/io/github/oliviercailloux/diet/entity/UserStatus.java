package io.github.oliviercailloux.diet.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import io.github.oliviercailloux.diet.VideoService;
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

	private final VideoService v;

	/**
	 * @param user must be persistent; the reference should not be used otherwise
	 *             (to guarantee immutability of the returned object).
	 */
	static UserStatus fromExistingUser(User user, VideoService v) {
		checkArgument(user.isPersistent());
		return new UserStatus(UserPersistentWithEvents.persistent(user), v);
	}

	static UserStatus fromFictitious(String username, Set<ReadEvent> events, VideoService v) {
		return new UserStatus(UserFictitious.fictitious(username, events), v);
	}

	static UserStatus fromIUser(IUser user, VideoService v) {
		return new UserStatus(user, v);
	}

	private UserStatus(IUser user, VideoService v) {
		this.user = checkNotNull(user);
		this.v = checkNotNull(v);
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
		final ImmutableSet<Video> seen = ImmutableSet.copyOf(user.readSeen());
		final ImmutableSet<Video> all = v.getAll();
		final ImmutableSet<Video> toSee = Sets.difference(all, seen).immutableCopy();
		return toSee;
	}
}
