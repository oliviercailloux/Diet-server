package io.github.oliviercailloux.sample_quarkus_heroku;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.io.Resources;
import io.github.oliviercailloux.sample_quarkus_heroku.dao.Base64;
import io.quarkus.test.junit.QuarkusTest;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class UserTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserTests.class);

	@Inject
	UserService service;

	@Test
	public void testNE() throws Exception {
		given().when().get("/v0/notexists").then().statusCode(404);
	}

	@Test
	@Transactional
	public void testNotLogged() throws Exception {
		given().get("/v0/me/status").then().statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
	}

	@Test
	@Transactional
	public void testBadLogIn() throws Exception {
		given().auth().basic("user", "incorrectpassword").get("/v0/me/status").then()
				.statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
	}

	@Test
	@Transactional
	public void testLogIn() throws Exception {
		final io.restassured.response.Response response = given().auth()
				.basic(Base64.from("user0").getRawBase64String(), Base64.from("user").getRawBase64String())
				.get("/v0/me/status");
		LOGGER.info("Log in yielded: {}.", response.asPrettyString());
		response.then().statusCode(Response.Status.OK.getStatusCode());
	}

	@Test
	@Transactional
	public void testStatusUser0() throws Exception {
		final String expected = Resources.toString(getClass().getResource("user0.json"), StandardCharsets.UTF_8);
		final io.restassured.response.Response response = given().auth()
				.basic(Base64.from("user0").getRawBase64String(), Base64.from("user").getRawBase64String())
//				.basic(Base64.from("inited").getRawBase64String(), Base64.from("user").getRawBase64String())
				.get("/v0/me/status");
		final String obtained = response.body().asPrettyString();
		assertEquals(expected, obtained);
	}

}