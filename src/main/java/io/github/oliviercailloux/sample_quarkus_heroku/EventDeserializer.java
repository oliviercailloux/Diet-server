package io.github.oliviercailloux.sample_quarkus_heroku;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class EventDeserializer extends StdDeserializer<Event> {
	public EventDeserializer() {
		this(null);
	}

	public EventDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Event deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		throw new JsonProcessingException("Need a user as context");
	}
}
