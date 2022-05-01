package io.github.oliviercailloux.diet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.diet.user.ReadEvent;
import io.github.oliviercailloux.diet.user.UserStatus;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import org.gradle.internal.impldep.com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserStatusJsonTests {

	@Inject
	Jsonb jsonb;

	@Test
	void test() {
		final UserStatus statusObj = UserStatus.fromFictitious("u", ImmutableSet.of(ReadEvent.accepted(Instant.now())),
				ImmutableSet.of());
		final String statusJson = jsonb.toJson(statusObj);
		assertEquals("""

				""", statusJson);
	}

}
