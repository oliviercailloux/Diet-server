package io.github.oliviercailloux.diet.video;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class VideoFactoryTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoFactoryTests.class);

	@Inject
	VideoFactory videoFactory;

	@Inject
	Client client;

	@Test
	@Transactional
	public void testRelations() throws Exception {
		final VideoWithCounters v1 = videoFactory.getWithCounters(1);
		final Video v3 = videoFactory.getVideo(3);
		final VideoWithCounters v9 = videoFactory.getWithCounters(9);
		final Video v10 = videoFactory.getVideo(10);
		final Video v11 = videoFactory.getVideo(11);
		assertEquals(ImmutableSet.of(v1, v3), v9.counters());
		assertEquals(ImmutableSet.of(), v9.counteredBy());
		assertEquals(ImmutableSet.of(v9, v10, v11), v1.counteredBy());
	}

	@Test
	@Transactional
	public void testGetSets() throws Exception {
		final ImmutableSet<Video> starters = videoFactory.getStarters();
		assertEquals(ImmutableSet.of(1, 2, 3, 4, 5, 6, 7, 8, 16),
				starters.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
		final ImmutableSet<Video> replies = videoFactory.getReplies(starters);
		assertEquals(ImmutableSet.of(9, 10, 11, 12, 13, 14, 15),
				replies.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
		assertEquals(ImmutableSet.of(), videoFactory
				.getReplies(starters.stream().filter(v -> v.getFileId() == 4).collect(ImmutableSet.toImmutableSet()))
				.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
		assertEquals(ImmutableSet.of(12, 13, 14, 15), videoFactory
				.getReplies(starters.stream().filter(v -> v.getFileId() == 2).collect(ImmutableSet.toImmutableSet()))
				.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
		assertEquals(ImmutableSet.of(9), videoFactory
				.getReplies(starters.stream().filter(v -> v.getFileId() == 3).collect(ImmutableSet.toImmutableSet()))
				.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
	}

	@Test
	@Transactional
	public void testDl() throws Exception {
		final ImmutableSet<Video> starters = videoFactory.getStarters();
		final URI url = starters.iterator().next().getUrl();
		LOGGER.info("Testing {}.", url);

		try (Response res = client.target(url).request().head()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
		}
	}

}