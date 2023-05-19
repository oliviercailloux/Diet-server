package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import jakarta.persistence.EntityManager;

public class VideoAppendable extends VideoWithCounters {
	static VideoAppendable fromPersistent(EntityManager em, VideoEntity video) {
		return new VideoAppendable(em, video);
	}

	private final EntityManager em;

	private VideoAppendable(EntityManager em, VideoEntity video) {
		super(video);
		this.em = checkNotNull(em);
		checkArgument(video.isPersistent());
		checkArgument(video.hasCounters());
	}

	public void persistCounters(VideoAppendable countered) {
		final ArguerAttack attack = new ArguerAttack(video(), countered.video());
		video().counters().add(attack);
		countered.addCounteredBy(attack);
		em.persist(attack);
	}

	void addCounteredBy(ArguerAttack attack) {
		video().counteredBy().add(attack);
	}
}
