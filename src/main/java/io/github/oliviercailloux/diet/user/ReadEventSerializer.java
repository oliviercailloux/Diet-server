package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Verify.verify;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadEventSerializer<T extends ReadEvent> implements JsonbSerializer<T> {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadEventSerializer.class);

	@SuppressWarnings("unused")
	public void writeMore(T event, JsonGenerator generator, SerializationContext ctx) {
	}

	@SuppressWarnings("resource")
	@Override
	public void serialize(T event, JsonGenerator generator, SerializationContext ctx) {
		LOGGER.info("Writing {}.", event);
		generator.writeStartObject();
		final String typeName = event.getClass().getSimpleName();
		verify(typeName.startsWith("ReadEvent"), typeName);
		generator.write("type", typeName.substring("ReadEvent".length()));
		generator.write("creation", event.creation().toString());
		writeMore(event, generator, ctx);
		generator.writeEnd();
	}
}
