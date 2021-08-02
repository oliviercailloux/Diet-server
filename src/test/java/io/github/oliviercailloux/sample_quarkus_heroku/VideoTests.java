package io.github.oliviercailloux.sample_quarkus_heroku;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Video;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class VideoTests {

	@Inject
	VideoService videoService;

	@Test
	public void testNE() {
		given().when().get("/v0/notexists").then().statusCode(404);
	}

	@Test
	@Transactional
	public void testGet() {
//		given().when().get("/v0/notexists").then().statusCode(404).body(Is.is(""));
		final ImmutableSet<Video> starters = videoService.getStarters();
		assertEquals(ImmutableSet.of(1, 2),
				starters.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
		final ImmutableSet<Video> replies = videoService.getReplies(starters);
		assertEquals(ImmutableSet.of(2), replies.stream().map(Video::getFileId).collect(ImmutableSet.toImmutableSet()));
	}

	@Test
	public void testPost() {
		given().post("/v0/items");
		given().when().get("/v0/items").then().statusCode(200).body(MatchesPattern.matchesPattern("MyItem dated .*"));
		given().post("/v0/items");
		given().when().get("/v0/items").then().statusCode(200)
				.body(MatchesPattern.matchesPattern("MyItem dated .*\nMyItem dated .*"));
	}

}