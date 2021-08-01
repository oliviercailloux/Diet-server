package io.github.oliviercailloux.sample_quarkus_heroku.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ArguerAttack {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Nonnull
	private Video video;

	@Nonnull
	private Video counters;

	ArguerAttack() {
		/* For JPA. */
	}

	public ArguerAttack(Video video, Video counters) {
		checkArgument(!video.equals(counters));
		this.video = checkNotNull(video);
		this.counters = checkNotNull(counters);
	}

	public Video getVideo() {
		return video;
	}

	public Video getCounters() {
		return counters;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("video", video).add("counters", counters).toString();
	}

}
