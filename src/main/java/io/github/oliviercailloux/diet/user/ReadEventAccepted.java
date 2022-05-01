package io.github.oliviercailloux.diet.user;

import java.time.Instant;

public class ReadEventAccepted extends ReadEvent {
	public static ReadEvent now() {
		return new ReadEventAccepted(new EventAccepted(Instant.now()));
	}

	public static ReadEvent fromEvent(EventAccepted event) {
		return new ReadEventAccepted(event);
	}

	private ReadEventAccepted(EventAccepted event) {
		super(event);
	}
}
