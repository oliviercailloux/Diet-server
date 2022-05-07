package io.github.oliviercailloux.diet.draw;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.video.VideoWithCounters;
import java.util.Set;

public class Drawer {
	public static Drawer instance(Set<VideoWithCounters> videos) {
		return new Drawer(videos);
	}

	private final ImmutableSet<VideoWithCounters> videos;

	private Drawer(Set<VideoWithCounters> videos) {
		this.videos = ImmutableSet.copyOf(videos);
	}
}
