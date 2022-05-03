package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import java.net.URI;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.persistence.EntityManager;

@JsonbPropertyOrder({ "fileId", "url", "description", "side", "countersFileIds" })
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

	Video video() {
		return video;
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

	public int getFileId() {
		return video.getFileId();
	}

	public URI getUrl() {
		return video.getUrl();
	}

	public String getDescription() {
		return video.getDescription();
	}

	public Side getSide() {
		return video.getSide();
	}

	public ImmutableSet<Integer> getCountersFileIds() {
		return video.getCountersFileIds();
	}
}
