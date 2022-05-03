package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.VideoAppendable;
import io.github.oliviercailloux.diet.video.VideoFactory;
import java.util.stream.Stream;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.persistence.EntityManager;

/**
 * A user with role User, thus, whose events start with an accepted event.
 */
@JsonbPropertyOrder({ "username", "events", "seen", "toSee" })
public class UserPersistentWithEvents implements RawUser {
	public static UserPersistentWithEvents fromExistingWithEvents(EntityManager em, VideoFactory v, User user) {
		return new UserPersistentWithEvents(em, v, user);
	}

	private final EntityManager em;
	private final VideoFactory videoFactory;
	private final User user;

	private UserPersistentWithEvents(EntityManager em, VideoFactory videoFactory, User user) {
		this.em = checkNotNull(em);
		this.videoFactory = checkNotNull(videoFactory);
		this.user = checkNotNull(user);
		checkArgument(user.isPersistent());
		checkArgument(user.hasEvents());
		checkArgument(user.role().equals(User.USER_ROLE));
	}

	@Override
	public String username() {
		return user.getUsername();
	}

	/**
	 * @return {@link User#USER_ROLE}
	 */
	@Override
	public String role() {
		return User.USER_ROLE;
	}

	/**
	 * @return with at least one accepted event, in order of creation
	 */
	public ImmutableSortedSet<ReadEvent> readEvents() {
		return user.events().stream().map(e -> ReadEvent.fromEvent(em, e))
				.collect(ImmutableSortedSet.toImmutableSortedSet(ReadEvent.COMPARATOR));
	}

	private Stream<VideoAppendable> seenStream() {
		return readEvents().stream().filter(e -> e instanceof ReadEventSeen).map(e -> (ReadEventSeen) e)
				.map(ReadEventSeen::video);
	}

	/**
	 * Returns the videos seen.
	 *
	 * @return the videos seen, in order seen
	 */
	public ImmutableList<VideoAppendable> readSeen() {
		return seenStream().collect(ImmutableList.toImmutableList());
	}

	/**
	 * Returns the video file ids seen.
	 *
	 * @return the video file ids seen, in order seen
	 */
	public ImmutableList<Integer> readSeenIds() {
		return seenStream().map(VideoAppendable::getFileId).collect(ImmutableList.toImmutableList());
	}

	public void persistEvent(ReadEvent event) {
		checkNotNull(event);
		checkArgument(!(event instanceof ReadEventAccepted));
		final Event underlyingEvent = event.underlyingEvent();
		underlyingEvent.user = user;
		user.events().add(underlyingEvent);
		em.persist(underlyingEvent);
	}

	public String getUsername() {
		return username();
	}

	public ImmutableSortedSet<ReadEvent> getEvents() {
		return readEvents();
	}

	public ImmutableList<VideoAppendable> getSeen() {
		return readSeen();
	}

	public ImmutableSet<VideoAppendable> getToSee() {
		final ImmutableSet<Integer> seenIds = ImmutableSet.copyOf(readSeenIds());
		final ImmutableSet<VideoAppendable> all = videoFactory.getAll();
		final ImmutableSet<VideoAppendable> toSee = all.stream().filter(vi -> !seenIds.contains(vi.getFileId()))
				.collect(ImmutableSet.toImmutableSet());
		return toSee;
	}

	@SuppressWarnings("unused")
	private ImmutableSet<VideoAppendable> getToSeeWithImmediateRepliesOnly() {
		final ImmutableSet<VideoAppendable> seen = ImmutableSet.copyOf(readSeen());
		final ImmutableSet<VideoAppendable> replies = videoFactory.getReplies(seen);
		final ImmutableSet<VideoAppendable> toSee = Sets
				.difference(Sets.union(videoFactory.getStarters(), replies), seen).immutableCopy();
		return toSee;
	}
}
