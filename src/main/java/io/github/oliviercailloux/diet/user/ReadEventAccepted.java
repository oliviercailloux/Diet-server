package io.github.oliviercailloux.diet.user;

import jakarta.json.bind.annotation.JsonbTypeSerializer;
import java.time.Instant;

@JsonbTypeSerializer(ReadEventSerializer.class)
public class ReadEventAccepted extends ReadEvent {
	public static ReadEvent now() {
		return at(Instant.now());
	}

	public static ReadEvent at(Instant creation) {
		return new ReadEventAccepted(new EventAccepted(creation));
	}

	public static ReadEvent fromEvent(EventAccepted event) {
		return new ReadEventAccepted(event);
	}

	private ReadEventAccepted(EventAccepted event) {
		super(event);
	}
}
