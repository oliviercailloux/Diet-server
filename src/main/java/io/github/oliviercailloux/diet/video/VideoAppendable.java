package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.persistence.EntityManager;

public class VideoAppendable {
	static VideoAppendable fromPersistent(EntityManager em, Video video) {
		return new VideoAppendable(em, video);
	}

	private final EntityManager em;
	private final Video video;

	private VideoAppendable(EntityManager em, Video video) {
		this.em = checkNotNull(em);
		this.video = checkNotNull(video);
		checkArgument(video.isPersistent());
		checkArgument(video.hasCounters());
	}

	public void persistCounters(VideoAppendable countered) {
		final ArguerAttack attack = new ArguerAttack(video, countered.video);
		video.counters().add(attack);
		countered.addCounteredBy(attack);
		em.persist(attack);
	}

	void addCounteredBy(ArguerAttack attack) {
		video.counteredBy().add(attack);
	}
}
