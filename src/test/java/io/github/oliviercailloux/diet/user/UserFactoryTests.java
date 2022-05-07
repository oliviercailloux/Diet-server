package io.github.oliviercailloux.diet.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.github.oliviercailloux.diet.video.Video;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserFactoryTests {

	@Inject
	UserFactory factory;

	@Test
	void testStatusUser0() throws Exception {
		final UserWithEvents user = factory.getWithEvents("user0");
		assertEquals("user0", user.getUsername());
		assertEquals(ImmutableList.of(), user.getSeen());
		final Video videoSeen0 = user.getToSee().asList().get(0);
		assertEquals(1, videoSeen0.getFileId());
	}

	@Test
	void testStatusUserSeen() throws Exception {
		final UserWithEvents user = factory.getWithEvents("seen");
		assertEquals("seen", user.getUsername());
		assertEquals(3, Iterables.getOnlyElement(user.getSeen()).getFileId());
		final Video videoSeen0 = user.getToSee().asList().get(0);
		assertEquals(1, videoSeen0.getFileId());
	}

}
