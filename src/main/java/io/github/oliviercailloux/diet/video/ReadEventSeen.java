package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Preconditions.checkArgument;

import io.github.oliviercailloux.diet.user.Event;
import io.github.oliviercailloux.diet.user.ReadEvent;
import io.github.oliviercailloux.diet.user.ReadEventSerializer;
import io.github.oliviercailloux.diet.video.ReadEventSeen.ReadEventSeenSerializer;
import java.time.Instant;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
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
		return new ReadEventSeen(new EventSeen(creation, video));
	}

	public static boolean accept(Event event) {
		return event instanceof EventSeen;
	}

	public static ReadEvent fromEvent(Event event) {
		checkArgument(accept(event));

		return new ReadEventSeen((EventSeen) event);
	}

	static ReadEvent fromEventSeen(EventSeen event) {
		return new ReadEventSeen(event);
	}

	private ReadEventSeen(EventSeen event) {
		super(event);
	}

	public Video video() {
		return ((EventSeen) underlyingEvent()).getVideo();
	}
}
