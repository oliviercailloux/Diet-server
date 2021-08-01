package io.github.oliviercailloux.sample_quarkus_heroku;

import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventAccepted;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventJudgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Judgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import java.time.Instant;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/me")
public class UserResource {

	@Context
	SecurityContext securityContext;

	@Inject
	UserService service;

	private String getCurrentUsername() {
		final String username = securityContext.getUserPrincipal().getName();
		return username;
	}

	private User getCurrentUser() {
		final String username = getCurrentUsername();
		return service.get(username);
	}

	@GET
	@RolesAllowed("user")
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON })
	public User status() {
		return getCurrentUser();
	}

	@PUT
	@RolesAllowed("user")
	@Path("/accept")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public User putAccept() throws WebApplicationException {
		final User user = getCurrentUser();
		if (!user.getEvents().isEmpty()) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		}

		final EventAccepted event = new EventAccepted(user, Instant.now());
		service.addEvent(event);
		return user;
	}

	@POST
	@RolesAllowed("user")
	@Path("/judgment")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public User postJudgment(Judgment judgment) throws WebApplicationException {
		final User user = getCurrentUser();
		if (user.getEvents().isEmpty()) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		}

		final EventJudgment event = new EventJudgment(user, Instant.now(), judgment);
		service.addEvent(event);
		return user;
	}
}