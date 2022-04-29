package io.github.oliviercailloux.diet;

import com.google.common.base.Stopwatch;
import io.github.oliviercailloux.diet.entity.User;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/test")
public class TestResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(TestResource.class);
	@Context
	SecurityContext securityContext;
	@Inject
	UserService userService;
	@Inject
	VideoService videoService;

	@GET
	@PermitAll
	@Path("/unauthenticated")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getUnauthenticated() {
		LOGGER.info("Getting unauthenticated.");
		return "Unauthenticated.";
	}

	@GET
	@PermitAll
	@Path("/perf")
	@Produces({ MediaType.TEXT_PLAIN })
	@Transactional
	public String getPerf() {
		LOGGER.info("Getting perf.");

		final Stopwatch stopwatch = Stopwatch.createStarted();
		userService.getWithoutEvents("user0");
		stopwatch.stop();
		LOGGER.warn("Time for one simple query: {} ms.", stopwatch.elapsed().toMillis());

		return "Perf.";
	}

	@GET
	@PermitAll
	@Path("/perfsSimple/{nbIterations}")
	@Produces({ MediaType.TEXT_PLAIN })
	@Transactional
	public String getPerfsSimple(@PathParam("nbIterations") int nbIterations) {
		LOGGER.info("Getting perfs.");
		final String queries = nbIterations == 1 ? "query" : "queries";
		{
			final Stopwatch stopwatch = Stopwatch.createStarted();
			for (int i = 0; i < nbIterations; ++i) {
				userService.getWithoutEvents("user0");
			}
			stopwatch.stop();
			LOGGER.warn("Average time over {} simple " + queries + ": {} ms.", nbIterations,
					stopwatch.elapsed().dividedBy(nbIterations).toMillis());
		}
		return "Perfs.";
	}

	@GET
	@PermitAll
	@Path("/perfs/{nbIterations}")
	@Produces({ MediaType.TEXT_PLAIN })
	@Transactional
	public String getPerfs(@PathParam("nbIterations") int nbIterations) {
		LOGGER.info("Getting perfs.");
		final String queries = nbIterations == 1 ? "query" : "queries";
		{
			final Stopwatch stopwatch = Stopwatch.createStarted();
			for (int i = 0; i < nbIterations; ++i) {
				userService.getWithoutEvents("user0");
			}
			stopwatch.stop();
			LOGGER.warn("Average time over {} simple " + queries + ": {} ms.", nbIterations,
					stopwatch.elapsed().dividedBy(nbIterations).toMillis());
		}
		{
			final Stopwatch stopwatch = Stopwatch.createStarted();
			for (int i = 0; i < nbIterations; ++i) {
				userService.get("user0");
			}
			stopwatch.stop();
			LOGGER.warn("Average time over {} complex " + queries + ": {} ms.", nbIterations,
					stopwatch.elapsed().dividedBy(nbIterations).toMillis());
		}
		{
			final Stopwatch stopwatch = Stopwatch.createStarted();
			for (int i = 0; i < nbIterations; ++i) {
				userService.getWithoutEvents("user0");
			}
			stopwatch.stop();
			LOGGER.warn("Average time over {} simple " + queries + ": {} ms.", nbIterations,
					stopwatch.elapsed().dividedBy(nbIterations).toMillis());
		}
		{
			final Stopwatch stopwatch = Stopwatch.createStarted();
			for (int i = 0; i < nbIterations; ++i) {
				videoService.getAll();
			}
			stopwatch.stop();
			LOGGER.warn("Average time over {} complex video " + queries + ": {} ms.", nbIterations,
					stopwatch.elapsed().dividedBy(nbIterations).toMillis());
		}
		return "Perfs.";
	}

	@GET
	@RolesAllowed("user")
	@Path("/authenticated")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getAuthenticated() {
		LOGGER.info("Getting authenticated.");
		return "Authenticated.";
	}

	@GET
	@RolesAllowed("user")
	@Path("/user0")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public User getUser0() {
		LOGGER.info("Getting user0.");
		return userService.get("user0");
	}

}
