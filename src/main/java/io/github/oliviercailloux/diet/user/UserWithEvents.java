package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.Video;
import io.github.oliviercailloux.diet.video.VideoAppendable;
import io.github.oliviercailloux.diet.video.VideoFactory;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import jakarta.persistence.EntityManager;
import java.util.stream.Stream;

/**
 * A user with role User, thus, whose events start with an accepted event.
 */
@JsonbPropertyOrder({ "username", "events", "seen", "toSee" })
public class UserWithEvents implements RawUser {
	public static UserWithEvents fromExistingWithEvents(EntityManager em, VideoFactory v, UserEntity user) {
		return new UserWithEvents(em, v, user);
	}

	private final EntityManager em;
	private final VideoFactory videoFactory;
	private final UserEntity user;
	private ImmutableSet<VideoAppendable> all;

	private UserWithEvents(EntityManager em, VideoFactory videoFactory, UserEntity user) {
		this.em = checkNotNull(em);
		this.videoFactory = checkNotNull(videoFactory);
		this.user = checkNotNull(user);
		checkArgument(user.isPersistent());
		checkArgument(user.hasEvents());
		checkArgument(user.role().equals(UserEntity.USER_ROLE));
		all = null;
	}

	@Override
	public String username() {
		return user.getUsername();
	}

	/**
	 * @return {@link UserEntity#USER_ROLE}
	 */
	@Override
	public String role() {
		return UserEntity.USER_ROLE;
	}

	/**
	 * @return with at least one accepted event, in order of creation
	 */
	public ImmutableSortedSet<ReadEvent> readEvents() {
		return user.events().stream().map(ReadEvent::fromEvent)
				.collect(ImmutableSortedSet.toImmutableSortedSet(ReadEvent.COMPARATOR));
	}

	private Stream<Video> seenStream() {
		return readEvents().stream().filter(e -> e instanceof ReadEventSeen).map(e -> (ReadEventSeen) e)
				.map(ReadEventSeen::video);
	}

	/**
	 * Returns the videos seen.
	 *
	 * @return the videos seen, in order seen
	 */
	public ImmutableList<Video> readSeen() {
		return seenStream().collect(ImmutableList.toImmutableList());
	}

	/**
	 * Returns the video file ids seen.
	 *
	 * @return the video file ids seen, in order seen
	 */
	public ImmutableList<Integer> readSeenIds() {
		return seenStream().map(Video::getFileId).collect(ImmutableList.toImmutableList());
	}

	public void persistEvent(ReadEvent event) {
		checkNotNull(event);
		checkArgument(!(event instanceof ReadEventAccepted));
		final Event underlyingEvent = event.underlyingEvent();
		underlyingEvent.user = user;
		user.events().add(underlyingEvent);
		event.persist(em);
	}

	public String getUsername() {
		return username();
	}

	public ImmutableSortedSet<ReadEvent> getEvents() {
		return readEvents();
	}

	private void lazyAll() {
		if (all == null) {
			all = videoFactory.getAll();
		}
	}

	public ImmutableList<VideoAppendable> getSeen() {
		lazyAll();
		final ImmutableList<Integer> seenIds = readSeenIds();
		final ImmutableBiMap<Integer, VideoAppendable> byId = all.stream()
				.collect(ImmutableBiMap.toImmutableBiMap(VideoAppendable::getFileId, v -> v));
		return seenIds.stream().map(byId::get).collect(ImmutableList.toImmutableList());
	}

	public ImmutableSet<VideoAppendable> getToSee() {
		lazyAll();
		final ImmutableSet<Integer> seenIds = ImmutableSet.copyOf(readSeenIds());
		final ImmutableSet<VideoAppendable> toSee = all.stream().filter(vi -> !seenIds.contains(vi.getFileId()))
				.collect(ImmutableSet.toImmutableSet());
		return toSee;
	}

	@SuppressWarnings("unused")
	private ImmutableSet<Video> getToSeeWithImmediateRepliesOnly() {
		final ImmutableSet<Video> seen = ImmutableSet.copyOf(readSeen());
		final ImmutableSet<Video> replies = videoFactory.getReplies(seen);
		final ImmutableSet<Video> toSee = Sets.difference(Sets.union(videoFactory.getStarters(), replies), seen)
				.immutableCopy();
		return toSee;
	}
}
