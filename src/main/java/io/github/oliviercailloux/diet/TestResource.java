package io.github.oliviercailloux.diet;

import io.github.oliviercailloux.diet.entity.User;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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

	@GET
	@PermitAll
	@Path("/unauthenticated")
	@Produces({ MediaType.TEXT_PLAIN })
	public String getUnauthenticated() {
		LOGGER.info("Getting unauthenticated.");
		return "Unauthenticated.";
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
