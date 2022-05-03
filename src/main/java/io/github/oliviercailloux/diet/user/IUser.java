package io.github.oliviercailloux.diet.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.VideoAppendable;
import java.util.stream.Stream;

/**
 * A user with role User, thus, whose events start with an accepted event.
 */
interface IUser extends RawUser {
	/**
	 * @return {@link User#USER_ROLE}
	 */
	@Override
	public String role();

	/**
	 * @return with at least one accepted event, in order of creation
	 */
	public ImmutableSortedSet<ReadEvent> readEvents();

	private Stream<VideoAppendable> seenStream() {
		return readEvents().stream().filter(e -> e instanceof ReadEventSeen).map(e -> (ReadEventSeen) e)
				.map(ReadEventSeen::video);
	}

	/**
	 * Returns the videos seen.
	 *
	 * @return the videos seen, in order seen
	 */
	default ImmutableList<VideoAppendable> readSeen() {
		return seenStream().collect(ImmutableList.toImmutableList());
	}

	/**
	 * Returns the video file ids seen.
	 *
	 * @return the video file ids seen, in order seen
	 */
	default ImmutableList<Integer> readSeenIds() {
		return seenStream().map(VideoAppendable::getFileId).collect(ImmutableList.toImmutableList());
	}
}
