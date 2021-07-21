package io.github.oliviercailloux.sample_quarkus_heroku.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class UserDeserializer extends StdDeserializer<User> {
	public UserDeserializer() {
		this(null);
	}

	public UserDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public User deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		final User user = jp.readValueAs(User.class);
		user.rectifyEvents();
		return user;
	}
}
