package io.github.oliviercailloux.diet.entity;

import java.time.Instant;

public class ReadEventSeen extends ReadEvent {
	public static ReadEvent now(Video video) {
		return new ReadEventSeen(new EventSeen(Instant.now(), video));
	}

	public static ReadEvent fromEvent(EventSeen event) {
		return new ReadEventSeen(event);
	}

	private ReadEventSeen(EventSeen event) {
		super(event);
	}
}
