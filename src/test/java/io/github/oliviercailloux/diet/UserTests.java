package io.github.oliviercailloux.diet;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.diet.quarkus.Authenticator;
import io.github.oliviercailloux.diet.user.Judgment;
import io.github.oliviercailloux.diet.user.Login;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
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
//			final Event eventStart = parser.next();
//			checkArgument(eventStart == Event.START_OBJECT, eventStart);

			checkArgument(parser.next() == Event.KEY_NAME);
			checkArgument(parser.getString().equals("type"));
			checkArgument(parser.next() == Event.VALUE_STRING);
			final String type = parser.getString();

			checkArgument(parser.next() == Event.KEY_NAME);
			checkArgument(parser.getString().equals("creation"));
			checkArgument(parser.next() == Event.VALUE_STRING);
			final String creationString = parser.getString();
			final Instant creation = Instant.parse(creationString);

//			final Event endEvent = parser.next();
//			if (endEvent == Event.END_OBJECT) {
//				return new BareEvent(type, creation, Optional.empty());
//			}
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
	public void testNotLogged() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();

		try (Response response = client.target(target).request().buildGet().invoke()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		}
	}

	@Test
	public void testBadLogin() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();

		try (Response response = client.target(target).register(new Authenticator("user", "incorrectpassword"))
				.request().buildGet().invoke()) {
			assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
		}
	}

	@Test
	public void testLogin() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();

		try (Response response = client.target(target).register(new Authenticator("user0", "user")).request().buildGet()
				.invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		}
	}

	@Test
	public void testLoginÉlevé() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/status").build();

		try (Response response = client.target(target).register(new Authenticator("élevé", "user")).request().buildGet()
				.invoke()) {
			assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
		}
	}

	@Test
	public void testAddWrongMediaType() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/create-accept").build();
		try (Response response = client.target(target).request(MediaType.TEXT_PLAIN).buildPut(Entity.text(""))
				.invoke()) {
			assertEquals(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), response.getStatus());
		}
	}

	@Test
	public void testAddWrongContent() throws Exception {
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/create-accept").build();
		try (Response response = client.target(target).request(MediaType.APPLICATION_JSON)
				.buildPut(Entity.json(Json.createValue("ploum"))).invoke()) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		}
	}

	@Test
	public void testAddWithColon() throws Exception {
		final Login login = new Login("test:add", "test user password");
		final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/create-accept").build();
		try (Response response = client.target(target).request(MediaType.APPLICATION_JSON).buildPut(Entity.json(login))
				.invoke()) {
			assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		}
	}

	@Test
	public void testAddThenStatus() throws Exception {
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
	public void testAddThenJudgeThenStatus() throws Exception {
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