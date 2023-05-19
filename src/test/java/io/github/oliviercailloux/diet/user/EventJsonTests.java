package io.github.oliviercailloux.diet.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.transaction.Transactional;
import org.gradle.internal.impldep.com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Disabled("None of these react as expected, perhaps jsonb crashes as the user has no events.")
class EventJsonTests {

	@Inject
	Jsonb jsonb;

	@SuppressWarnings("serial")
	@Test
	@Transactional
	void testWithEvents() throws Exception {
		final String username = "testWithEvents " + Instant.now().toString().replace(":", "");
		final Login login = new Login(username, "user");
		final UserEntity user = new UserEntity(login, "user");
		final Instant created = Instant.now();
		final EventAccepted e = new EventAccepted(user, created);
		final ImmutableList<EventAccepted> es = ImmutableList.of(e);
//		final String eventJsonRaw = jsonb.toJson(e);
//		final String eventJsonE = jsonb.toJson(e, new Event().getClass());
//		final String eventJson = jsonb.toJson(e, new EventAccepted().getClass());
//		final String eventsJsonA = jsonb.toJson(es, new ArrayList<EventAccepted>() {
//		}.getClass());
		final String eventsJsonE = jsonb.toJson(es, new ArrayList<Event>() {
		}.getClass());
		final String expected = Files.readString(Path.of(this.getClass().getResource("accepted event.json").toURI()));
		assertEquals(expected.formatted(created), eventsJsonE);
	}

	@SuppressWarnings("serial")
	@Test
	@Transactional
	void testUserWithEvents() throws Exception {
		final String username = "testWithEvents " + Instant.now().toString().replace(":", "");
		final Login login = new Login(username, "user");
		final UserEntity user = new UserEntity(login, "user");
		final Instant created = Instant.now();
//		final EventAccepted e = new EventAccepted(user, created);
//		final ImmutableList<EventAccepted> es = ImmutableList.of(e);
//		final String eventJsonRaw = jsonb.toJson(e);
//		final String eventJsonE = jsonb.toJson(e, new Event().getClass());
//		final String eventJson = jsonb.toJson(e, new EventAccepted().getClass());
//		final String eventsJsonA = jsonb.toJson(es, new ArrayList<EventAccepted>() {
//		}.getClass());
		final String u = jsonb.toJson(user);
		final String expected = Files.readString(Path.of(this.getClass().getResource("accepted event.json").toURI()));
		assertEquals(expected.formatted(created), u);
	}

}
