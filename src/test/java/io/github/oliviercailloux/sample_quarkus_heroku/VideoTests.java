package io.github.oliviercailloux.sample_quarkus_heroku;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Video;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class VideoTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoTests.class);

	@Inject
	VideoService videoService;

	@Test
	@Transactional
	public void testGet() throws Exception {
		final ImmutableSet<Video> starters = videoService.getStarters();
		assertEquals(ImmutableSet.of(1, 2, 3, 4, 5, 6, 7, 8, 16),
				starters.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
		final ImmutableSet<Video> replies = videoService.getReplies(starters);
		assertEquals(ImmutableSet.of(9, 10, 11, 12, 13, 14, 15),
				replies.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
		assertEquals(ImmutableSet.of(), videoService
				.getReplies(starters.stream().filter(v -> v.getFileId() == 4).collect(ImmutableSet.toImmutableSet()))
				.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
		assertEquals(ImmutableSet.of(12, 13, 14, 15), videoService
				.getReplies(starters.stream().filter(v -> v.getFileId() == 2).collect(ImmutableSet.toImmutableSet()))
				.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
		assertEquals(ImmutableSet.of(9), videoService
				.getReplies(starters.stream().filter(v -> v.getFileId() == 3).collect(ImmutableSet.toImmutableSet()))
				.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
	}

	@Test
	@Transactional
	public void testDl() throws Exception {
		final ImmutableSet<Video> starters = videoService.getStarters();
		final URI url = starters.iterator().next().getUrl();
		LOGGER.info("Testing {}.", url);
//		given().when().get(url).then().statusCode(Response.Status.OK.getStatusCode()).contentType(ContentType.BINARY);

		final Client client = ClientBuilder.newClient();
		try (Response res = client.target(url).request().head()) {
			assertEquals(Response.Status.OK.getStatusCode(), res.getStatus());
		}
	}

}