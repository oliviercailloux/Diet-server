package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import io.github.oliviercailloux.diet.video.VideoAppendable;
import io.github.oliviercailloux.diet.video.VideoFactory;
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

	private final VideoFactory videoFactory;

	/**
	 * @param user must be persistent; the reference should not be used otherwise
	 *             (to guarantee immutability of the returned object).
	 */
	static UserStatus fromExistingUser(User user, VideoFactory v) {
		checkArgument(user.isPersistent());
		return new UserStatus(UserPersistentWithEvents.persistent(user), v);
	}

	static UserStatus fromFictitious(String username, Set<ReadEvent> events, VideoFactory v) {
		return new UserStatus(UserFictitiousWithEvents.fictitious(username, events), v);
	}

	static UserStatus fromIUser(IUser user, VideoFactory v) {
		return new UserStatus(user, v);
	}

	private UserStatus(IUser user, VideoFactory v) {
		this.user = checkNotNull(user);
		this.videoFactory = checkNotNull(v);
	}

	public String getUsername() {
		return user.username();
	}

	public ImmutableSortedSet<ReadEvent> getEvents() {
		return user.readEvents();
	}

	public ImmutableList<VideoAppendable> getSeen() {
		return user.readSeen();
	}

	public ImmutableSet<VideoAppendable> getToSee() {
		final ImmutableSet<Integer> seenIds = ImmutableSet.copyOf(user.readSeenIds());
		final ImmutableSet<VideoAppendable> all = videoFactory.getAll();
		final ImmutableSet<VideoAppendable> toSee = all.stream().filter(vi -> !seenIds.contains(vi.getFileId()))
				.collect(ImmutableSet.toImmutableSet());
		return toSee;
	}

	@SuppressWarnings("unused")
	private ImmutableSet<VideoAppendable> getToSeeWithImmediateRepliesOnly() {
		final ImmutableSet<VideoAppendable> seen = ImmutableSet.copyOf(user.readSeen());
		final ImmutableSet<VideoAppendable> replies = videoFactory.getReplies(seen);
		final ImmutableSet<VideoAppendable> toSee = Sets
				.difference(Sets.union(videoFactory.getStarters(), replies), seen).immutableCopy();
		return toSee;
	}
}
