package io.github.oliviercailloux.diet.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

/**
 * A user with role User, thus, whose events start with an accepted event.
 */
interface IUser {
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
