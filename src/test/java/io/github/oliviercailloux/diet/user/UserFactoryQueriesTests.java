package io.github.oliviercailloux.diet.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.github.oliviercailloux.diet.video.Video;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@QuarkusTest
public class UserFactoryQueriesTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserFactoryQueriesTests.class);

	@Inject
	SessionFactory sessionFactory;

	@Inject
	UserFactory factory;

	@Test
	void testQueriesUser0() throws Exception {
		final Statistics stats = sessionFactory.getStatistics();
		final long queryCountStart = stats.getQueryExecutionCount();

		final UserWithEvents user = factory.getWithEvents("user0");
		assertEquals("user0", user.getUsername());
		{
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(1, nbQueries);
		}
		assertEquals(ImmutableList.of(), user.getSeen());
		{
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(2, nbQueries);
		}
		final Video videoSeen0 = user.getToSee().asList().get(0);
		assertEquals(1, videoSeen0.getFileId());
		{
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(2, nbQueries);
		}
	}

	@Test
	void testQueriesUserSeen() throws Exception {
		final Statistics stats = sessionFactory.getStatistics();
		final long queryCountStart = stats.getQueryExecutionCount();

		final UserWithEvents user = factory.getWithEvents("seen");
		assertEquals("seen", user.getUsername());
		{
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(1, nbQueries);
		}
		assertEquals(3, Iterables.getOnlyElement(user.getSeen()).getFileId());
		{
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(2, nbQueries);
		}
		final Video videoSeen0 = user.getToSee().asList().get(0);
		assertEquals(1, videoSeen0.getFileId());
		{
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(2, nbQueries);
		}
	}

	@Test
	@Transactional
	void testGetFromCache() throws Exception {
		final Statistics stats = sessionFactory.getStatistics();
		final long queryCountStart = stats.getQueryExecutionCount();

		{
			final RawUser user = factory.getWithoutEvents("user0");
			assertFalse(((User) user).user.hasEvents());
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(1, nbQueries);
		}

		{
			factory.getWithEvents("user0");
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(2, nbQueries);
		}

		{
			final RawUser user = factory.getWithoutEvents("user0");
			final long nbQueries = stats.getQueryExecutionCount() - queryCountStart;
			assertEquals(3, nbQueries);
			assertTrue(((User) user).user.hasEvents());
		}
	}

}
