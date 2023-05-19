package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.diet.user.Event;
import io.github.oliviercailloux.diet.user.ReadEvent;
import io.github.oliviercailloux.diet.user.ReadEventSerializer;
import io.github.oliviercailloux.diet.video.ReadEventSeen.ReadEventSeenSerializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonbTypeSerializer(ReadEventSeenSerializer.class)
public class ReadEventSeen extends ReadEvent {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadEventSeen.class);

	public static class ReadEventSeenSerializer extends ReadEventSerializer<ReadEventSeen> {
		@Override
		public void writeMore(ReadEventSeen event, JsonGenerator generator, SerializationContext ctx) {
			LOGGER.info("Writing more about {}.", event);
			ctx.serialize("fileId", event.video().getFileId(), generator);
		}
	}

	public static ReadEventSeen now(Video video) {
		return at(Instant.now(), video);
	}

	public static ReadEventSeen at(Instant creation, Video video) {
		return new ReadEventSeen(new EventSeen(creation, video.video()), video);
	}

	public static boolean accept(Event event) {
		return event instanceof EventSeen;
	}

	public static ReadEvent fromEventSeen(Event event) {
		checkArgument(accept(event));
		final EventSeen eventSeen = (EventSeen) event;
		return fromEventSeenInternal(eventSeen);
	}

	static ReadEvent fromEventSeenInternal(EventSeen event) {
		return new ReadEventSeen(event, Video.fromPersistent(event.getVideo()));
	}

	private final Video video;

	private ReadEventSeen(EventSeen event, Video video) {
		super(event);
		this.video = checkNotNull(video);
		checkArgument(event.getVideo().equals(video.video()));
	}

	public Video video() {
		return video;
	}
}
