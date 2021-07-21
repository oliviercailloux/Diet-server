package io.github.oliviercailloux.sample_quarkus_heroku;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
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
		JsonNode node = jp.getCodec().readTree(jp);
		int id = (Integer) ((IntNode) node.get("id")).numberValue();
		String itemName = node.get("itemName").asText();
		int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();

		return new Item(id, itemName, new User(userId, null));
	}
}