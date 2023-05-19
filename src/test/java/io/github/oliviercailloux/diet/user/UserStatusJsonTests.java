package io.github.oliviercailloux.diet.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.VideoFactory;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserStatusJsonTests {

	@Inject
	Jsonb jsonb;
	@Inject
	VideoFactory videoFactory;
	@Inject
	UserFactory userFactory;

	@Test
	@Transactional
	void testWithEvents() throws Exception {
		final String username = "testWithEvents " + Instant.now().toString().replace(":", "");
		final Instant e1 = Instant.parse("2000-01-20T10:10:10.000000000Z");
		final Instant e2 = e1.plusSeconds(1);
		final Instant e3 = e2.plusSeconds(1);
		final UserWithEvents user;
		{
			user = userFactory.addUser(new Login(username, "user"), e1);
			final Judgment judgment = new Judgment(3, 0);
			user.persistEvent(ReadEventJudgment.at(e2, judgment));
			user.persistEvent(ReadEventSeen.at(e3, videoFactory.getVideo(6)));
		}
		final String statusJson = jsonb.toJson(user);
		final String expected = Files.readString(Path.of(this.getClass().getResource("seen counters.json").toURI()));
		assertEquals(expected.formatted(username, e1, e2, e3), statusJson);
	}

}
