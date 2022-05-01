package io.github.oliviercailloux.diet.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import io.github.oliviercailloux.diet.video.Video;

/**
 * A user with role User, thus, whose events start with an accepted event.
 */
interface IUser extends RawUser {
	public String username();

	/**
	 * @return with at least one accepted event, in order of creation
	 */
	public ImmutableSortedSet<ReadEvent> readEvents();

	/**
	 * Returns the videos seen.
	 *
	 * @return the videos seen, in order seen
	 */
	default ImmutableList<Video> readSeen() {
		return readEvents().stream().filter(ReadEvent::isSeen).map(ReadEvent::videoSeen)
				.collect(ImmutableList.toImmutableList());
	}

	/**
	 * Returns the video file ids seen.
	 *
	 * @return the video file ids seen, in order seen
	 */
	default ImmutableList<Integer> readSeenIds() {
		return readEvents().stream().filter(ReadEvent::isSeen).map(ReadEvent::videoSeen).map(Video::getFileId)
				.collect(ImmutableList.toImmutableList());
	}
}
