package io.github.oliviercailloux.diet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.diet.quarkus.Authenticator;
import io.github.oliviercailloux.diet.user.Judgment;
import io.github.oliviercailloux.diet.user.Login;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
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

	public static record UserStatus(String username, List<String> events, List<String> seen, List<String> toSee) {
		@JsonbCreator
		public UserStatus(@JsonbProperty("username") String username, @JsonbProperty("events") List<String> events,
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

	@Inject
	UserFactory service;

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
//			.body()
			final URI target = UriBuilder.fromUri(serverUri).path("/v0/me/judgment").build();
			try (Response response = client.target(target).register(new Authenticator(login)).request()
					.buildPost(Entity.entity(new Judgment(3, 1), MediaType.APPLICATION_JSON)).invoke()) {
				assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
				LOGGER.info("Judgment: {}.", response.readEntity(String.class));
				final UserStatus obtained = response.readEntity(UserStatus.class);
				LOGGER.info("Judgment as status: {}.", obtained);
				assertEquals(username, obtained.username);
				assertEquals(2, obtained.events.size());
				assertEquals("{ \"daysVegan\": 3,\"daysMeat\": 1}", obtained.events.get(1));
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
	public void testStatusUser0Internal() throws Exception {
//		final User user0 = service.get("user0");
//		final UserStatus status = service.get(user0);
//		assertEquals("user0", status.getUsername());
//		assertEquals(ImmutableSet.of(), status.getSeen());
//		final Video videoSeen0 = status.getToSee().asList().get(0);
//		assertEquals(1, videoSeen0.getFileId());
//		assertEquals(ImmutableSet.of(), videoSeen0.getCounters());
//		assertEquals(ImmutableSet.of(), videoSeen0.getCountersFileIds());
	}

	@Test
	public void testStatusUserSeenInternal() throws Exception {
//		final User user = service.get("seen");
//		final UserStatus status = service.get(user);
//		assertEquals("seen", status.getUsername());
//		final Video videoSeen = Iterables.getOnlyElement(status.getSeen());
//		assertEquals(3, videoSeen.getFileId());
//		assertEquals(ImmutableSet.of(), videoSeen.getCounters());
//		assertEquals(ImmutableSet.of(), videoSeen.getCountersFileIds());
	}

}