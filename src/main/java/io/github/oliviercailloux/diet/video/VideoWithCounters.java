package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableSet;
import java.util.stream.Stream;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "fileId", "url", "description", "side", "countersFileIds" })
public class VideoWithCounters extends Video {
	static VideoWithCounters fromPersistent(VideoEntity video) {
		return new VideoWithCounters(video);
	}

	protected VideoWithCounters(VideoEntity video) {
		super(video);
		checkArgument(video.isPersistent());
		checkArgument(video.hasCounters());
	}

	private Stream<VideoEntity> getCountersStream() {
		return video().counters().stream().map(ArguerAttack::getCounters);
	}

	public ImmutableSet<Integer> getCountersFileIds() {
		return getCountersStream().map(VideoEntity::getFileId).sorted().collect(ImmutableSet.toImmutableSet());
	}
}
