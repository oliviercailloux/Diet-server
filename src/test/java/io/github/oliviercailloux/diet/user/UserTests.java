package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.diet.quarkus.Authenticator;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class UserTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserTests.class);

	@JsonbTypeDeserializer(EventDeserializer.class)
	public static record BareEvent(String type, Instant creation, Optional<Judgment> judgment,
			Optional<Integer> fileId) {
	}

	public static class EventDeserializer implements JsonbDeserializer<BareEvent> {

		@Override
		public BareEvent deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
			checkArgument(parser.next() == Event.KEY_NAME);
			checkArgument(parser.getString().equals("type"));
			checkArgument(parser.next() == Event.VALUE_STRING);
			final String type = parser.getString();

			checkArgument(parser.next() == Event.KEY_NAME);
			checkArgument(parser.getString().equals("creation"));
			checkArgument(parser.next() == Event.VALUE_STRING);
			final String creationString = parser.getString();
			final Instant creation = Instant.parse(creationString);

			final Optional<Judgment> judgmentOpt;
			final Optional<Integer> fileIdOpt;
			final Event next = parser.next();
			if (next != Event.END_OBJECT) {
				checkArgument(next == Event.KEY_NAME, next);
				final String key = parser.getString();
				if (key.equals("judgment")) {
					final Judgment judgment = ctx.deserialize(Judgment.class, parser);
					judgmentOpt = Optional.of(judgment);
					fileIdOpt = Optional.empty();
				} else if (key.equals("fileId")) {
					judgmentOpt = Optional.empty();
					checkArgument(parser.next() == Event.VALUE_NUMBER);
					final int fileId = parser.getInt();
					fileIdOpt = Optional.of(fileId);
				} else {
					throw new IllegalArgumentException();
				}
			} else {
				judgmentOpt = Optional.empty();
				fileIdOpt = Optional.empty();
			}

			return new BareEvent(type, creation, judgmentOpt, fileIdOpt);
		}
	}

	public static record UserStatus(String username, List<BareEvent> events, List<String> seen, List<String> toSee) {
		@JsonbCreator
		public UserStatus(@JsonbProperty("username") String username, @JsonbProperty("events") List<BareEvent> events,
				@JsonbProperty("seen") List<String> seen, @JsonbProperty("toSee") List<String> toSee) {
			this.username = username;
			this.events = events;
			this.seen = seen;
			this.toSee = toSee;
		}
	}

	@TestHTTPResource
	URI serverUri;
	@Inject
	Client client;

	@Test
	void testNotLogged() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();

		try (Response response = client.target(target).request().buildGet().invoke()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		}
	}

	@Test
	void testBadLogin() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();

		try (Response response = client.target(target).register(new Authenticator("user", "incorrectpassword"))
				.request().buildGet().invoke()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		}
	}

	@Test
	void testLogin() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();

		try (Response response = client.target(target).register(new Authenticator("user0", "user")).request().buildGet()
				.invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		}
	}

	@Test
	void testLoginÉlevé() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();

		try (Response response = client.target(target).register(new Authenticator("élevé", "user")).request().buildGet()
				.invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		}
	}

	@Test
	void testAddWrongMediaType() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/create-accept").build();
		try (Response response = client.target(target).request().buildPut(Entity.text("")).invoke()) {
			assertEquals(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), response.getStatus());
		}
	}

	@Test
	void testAddWrongContent() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/create-accept").build();
		try (Response response = client.target(target).request().buildPut(Entity.json(Json.createValue("ploum")))
				.invoke()) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		}
	}

	@Test
	void testAddWithColon() throws Exception {
		final Login login = new Login("test:add", "test user password");
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/create-accept").build();
		try (Response response = client.target(target).request(MediaType.APPLICATION_JSON).buildPut(Entity.json(login))
				.invoke()) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		}
	}

	@Test
	void testAddThenStatus() throws Exception {
		final String username = "testAdd " + Instant.now().toString().replace(":", "");
		final Login login = new Login(username, "test user password");
		{
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/create-accept").build();
			try (Response response = client.target(target).request(MediaType.APPLICATION_JSON)
					.buildPut(Entity.json(login)).invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
			}
		}

		{
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();
			try (Response response = client.target(target).register(new Authenticator(login)).request().buildGet()
					.invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//				final String expectedString = Resources.toString(getClass().getResource("user0.json"),
//						StandardCharsets.UTF_8);
//				final StringReader reader = new StringReader(expectedString);
//				final JsonReader read = Json.createReader(reader);
//				final JsonObject expected = read.readObject();
//				final JsonObject obtained = response.readEntity(JsonObject.class);
				final UserStatus obtained = response.readEntity(UserStatus.class);
				LOGGER.info("Status: {}.", obtained);
				assertEquals(username, obtained.username);
				assertEquals(1, obtained.events.size());
				assertEquals(0, obtained.seen.size());
				assertEquals(16, obtained.toSee.size());
			}
		}
	}

	@Test
	void testAddThenJudgeThenStatus() throws Exception {
		final String username = "testAddJudge " + Instant.now().toString().replace(":", "");
		final Login login = new Login(username, "test user password");
		{
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/create-accept").build();
			try (Response response = client.target(target).request(MediaType.APPLICATION_JSON)
					.buildPut(Entity.json(login)).invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
			}
		}

		{
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/judgment").build();
			final Judgment judgment = new Judgment(3, 1);
			try (Response response = client.target(target).register(new Authenticator(login)).request()
					.buildPost(Entity.json(judgment)).invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
				response.bufferEntity();
				LOGGER.debug("Judgment: {}.", response.readEntity(String.class));
				final UserStatus obtained = response.readEntity(UserStatus.class);
				LOGGER.debug("Judgment as status: {}.", obtained);
				assertEquals(username, obtained.username);
				assertEquals(2, obtained.events.size());
				assertEquals(judgment, obtained.events.get(1).judgment.get());
				assertEquals(0, obtained.seen.size());
				assertEquals(16, obtained.toSee.size());
			}
		}

		{
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();
			try (Response response = client.target(target).register(new Authenticator(login)).request().buildGet()
					.invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
				final UserStatus obtained = response.readEntity(UserStatus.class);
				LOGGER.info("Status: {}.", obtained);
				assertEquals(username, obtained.username);
				assertEquals(2, obtained.events.size());
				assertEquals(0, obtained.seen.size());
				assertEquals(16, obtained.toSee.size());
			}
		}
	}

	@Test
	public void testAddThenJudgeThenSeenThenStatus() throws Exception {
		final String username = "testAddJudge " + Instant.now().toString().replace(":", "");
		final Login login = new Login(username, "test user password");
		{
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/create-accept").build();
			try (Response response = client.target(target).request(MediaType.APPLICATION_JSON)
					.buildPut(Entity.json(login)).invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
			}
		}

		final Judgment judgment = new Judgment(0, 0);
		{
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/judgment").build();
			try (Response response = client.target(target).register(new Authenticator(login)).request()
					.buildPost(Entity.json(judgment)).invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
				response.bufferEntity();
				LOGGER.debug("Judgment: {}.", response.readEntity(String.class));
				final UserStatus obtained = response.readEntity(UserStatus.class);
				LOGGER.debug("Judgment as status: {}.", obtained);
				assertEquals(username, obtained.username);
				assertEquals(2, obtained.events.size());
				assertEquals(judgment, obtained.events.get(1).judgment.get());
				assertEquals(0, obtained.seen.size());
				assertEquals(16, obtained.toSee.size());
			}
		}

		{
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/video/4").build();
			try (Response response = client.target(target).register(new Authenticator(login)).request()
					.buildPut(Entity.text("")).invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
				response.bufferEntity();
				LOGGER.debug("Seen: {}.", response.readEntity(String.class));
				final UserStatus obtained = response.readEntity(UserStatus.class);
				LOGGER.debug("Seen as status: {}.", obtained);
				assertEquals(username, obtained.username);
				assertEquals(3, obtained.events.size());
				assertEquals(judgment, obtained.events.get(1).judgment.get());
				assertEquals(4, obtained.events.get(2).fileId.get());
				assertEquals(1, obtained.seen.size());
				assertEquals(15, obtained.toSee.size());
			}
		}

		{
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();
			try (Response response = client.target(target).register(new Authenticator(login)).request().buildGet()
					.invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
				final UserStatus obtained = response.readEntity(UserStatus.class);
				LOGGER.debug("Status: {}.", obtained);
				assertEquals(username, obtained.username);
				assertEquals(3, obtained.events.size());
				assertEquals(judgment, obtained.events.get(1).judgment.get());
				assertEquals(4, obtained.events.get(2).fileId.get());
				assertEquals(1, obtained.seen.size());
				assertEquals(15, obtained.toSee.size());
			}
		}
	}

}