package io.github.oliviercailloux.diet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.quarkus.Authenticator;
import io.github.oliviercailloux.diet.user.Login;
import io.github.oliviercailloux.diet.user.StaticUserStatus;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.json.Json;
import javax.transaction.Transactional;
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

	public static record UserStatus(String username, Set<String> events, List<String> seen, List<String> toSee) {

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
		final String username = "testAdd " + Instant.now().toString();
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

	/**
	 * Should use a new user to not change the status of a basic one.
	 */
	@Test
	@Transactional
	public void testJudge() throws Exception {
		final io.restassured.response.Response response = given().auth().basic("accepted", "user")
				.contentType(MediaType.APPLICATION_JSON).body("{ \"daysVegan\": 1,\"daysMeat\": 2}")
				.post("/v0/me/judgment");
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
		/*
		 * Deserialization does not work yet: events are not supported because of
		 * polymorphism.
		 */
//		final StaticUserStatus obtained = response.as(StaticUserStatus.class);
//		assertEquals("accepted", obtained.getUsername());
//		assertEquals("", obtained.getEvents());
	}

	@Test
	@Transactional
	public void testCreateAccept() throws Exception {
		final String username = "test-create-accept-username";
		final io.restassured.response.Response response = given().contentType(MediaType.APPLICATION_JSON)
				.body("{ \"username\": \"" + username + "\", \"password\": \"test-create-accept-password\"}")
				.put("/v0/me/create-accept");
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
		final String str = response.body().asPrettyString();
		LOGGER.info("Resp create accept: {}.", str);
		final StaticUserStatus obtained = response.as(StaticUserStatus.class);
		assertEquals(username, obtained.getUsername());
		assertEquals(ImmutableSet.of(), obtained.getSeen());
	}

}