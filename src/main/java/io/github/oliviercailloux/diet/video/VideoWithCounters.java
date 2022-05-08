package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableSortedSet;
import java.util.Comparator;
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

	private Stream<VideoEntity> countersStream() {
		return video().counters().stream().map(ArguerAttack::getCounters);
	}

	private Stream<VideoEntity> counteredByStream() {
		return video().counteredBy().stream().map(ArguerAttack::getVideo);
	}

	public ImmutableSortedSet<Video> counters() {
		return countersStream().map(Video::fromPersistent)
				.collect(ImmutableSortedSet.toImmutableSortedSet(Comparator.comparing(Video::getFileId)));
	}

	public ImmutableSortedSet<Integer> getCountersFileIds() {
		return countersStream().map(VideoEntity::getFileId).sorted()
				.collect(ImmutableSortedSet.toImmutableSortedSet(Comparator.naturalOrder()));
	}

	public ImmutableSortedSet<Video> counteredBy() {
		return counteredByStream().map(Video::fromPersistent)
				.collect(ImmutableSortedSet.toImmutableSortedSet(Comparator.comparing(Video::getFileId)));
	}
}
