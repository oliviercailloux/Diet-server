package io.github.oliviercailloux.diet.video;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class VideoFactoryQueriesTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoFactoryQueriesTests.class);

	@Inject
	SessionFactory sessionFactory;

	@Inject
	VideoFactory videoFactory;

	@Test
	@Transactional
	public void testGetFromCache() throws Exception {
		final Statistics stats = sessionFactory.getStatistics();
		final long queryCountStart = stats.getQueryExecutionCount();

		{
			final Video v3 = videoFactory.getVideo(3);
			assertFalse(v3.video().hasCounters());
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(1, nbQueries);
		}

		{
			videoFactory.getAll();
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(2, nbQueries);
		}

		{
			final Video v3 = videoFactory.getVideo(3);
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(3, nbQueries);
			assertTrue(v3.video().hasCounters());
		}
	}

	@Test
	@Transactional
	public void testGetAll() throws Exception {
		final Statistics stats = sessionFactory.getStatistics();
		final long queryCountStart = stats.getQueryExecutionCount();

		videoFactory.getAll();

		final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
		assertEquals(1, nbQueries);
	}

}