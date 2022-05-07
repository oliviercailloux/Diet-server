package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import java.util.stream.Stream;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.persistence.EntityManager;

//@JsonIgnoreProperties(value = { "url" }, allowGetters = true)
@JsonbPropertyOrder({ "fileId", "url", "description", "side", "countersFileIds" })
public class VideoWithCounters extends Video {
	static VideoWithCounters fromPersistent(EntityManager em, VideoEntity video) {
		return new VideoWithCounters(em, video);
	}

	private final EntityManager em;

	private VideoWithCounters(EntityManager em, VideoEntity video) {
		super(video);
		this.em = checkNotNull(em);
		checkArgument(video.isPersistent());
		checkArgument(video.hasCounters());
	}

	private Stream<VideoEntity> getCountersStream() {
		return video().counters().stream().map(ArguerAttack::getCounters);
	}

	public ImmutableSet<Integer> getCountersFileIds() {
		return getCountersStream().map(VideoEntity::getFileId).sorted().collect(ImmutableSet.toImmutableSet());
	}

	public void persistCounters(VideoWithCounters countered) {
		final ArguerAttack attack = new ArguerAttack(video(), countered.video());
		video().counters().add(attack);
		countered.addCounteredBy(attack);
		em.persist(attack);
	}

	void addCounteredBy(ArguerAttack attack) {
		video().counteredBy().add(attack);
	}
}
