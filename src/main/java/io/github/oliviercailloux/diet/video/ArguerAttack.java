package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class ArguerAttack {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	@ManyToOne
	private VideoEntity video;

	@NotNull
	@ManyToOne
	private VideoEntity counters;

	ArguerAttack() {
		/* For JPA. */
	}

	public ArguerAttack(VideoEntity video, VideoEntity counters) {
		checkArgument(!video.equals(counters));
		this.video = checkNotNull(video);
		this.counters = checkNotNull(counters);
	}

	public VideoEntity getVideo() {
		return video;
	}

	public VideoEntity getCounters() {
		return counters;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", id).add("video file id", video.getFileId())
				.add("counters file id", counters.getFileId()).toString();
	}

}
