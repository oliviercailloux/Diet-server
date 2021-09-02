package io.github.oliviercailloux.diet;

import io.github.oliviercailloux.diet.dao.Base64;
import io.github.oliviercailloux.diet.dao.Login;
import io.github.oliviercailloux.diet.dao.UserStatus;
import io.github.oliviercailloux.diet.entity.EventAccepted;
import io.github.oliviercailloux.diet.entity.EventJudgment;
import io.github.oliviercailloux.diet.entity.Judgment;
import io.github.oliviercailloux.diet.entity.User;
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
	UserService userService;

	private Base64 getCurrentUsername() {
		final String username = securityContext.getUserPrincipal().getName();
		return Base64.alreadyBase64(username);
	}

	private User getCurrentUser() {
		final Base64 username = getCurrentUsername();
		return userService.get(username);
	}

	@GET
	@RolesAllowed("user")
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserStatus status() {
		final User user = getCurrentUser();
		return userService.getStatus(user);
	}

	/**
	 * Idempotent: when body changes, new user is created; otherwise, does not
	 * create new user.
	 *
	 * This should return a HTTP created, I suppose, and be based at the root.
	 */
	@PUT
	@Path("/create-accept")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserStatus createAcceptingUser(Login login) throws WebApplicationException {
		final User user = userService.addUser(login);
		final EventAccepted event = new EventAccepted(user, Instant.now());
		userService.addSimpleEvent(event);
		return userService.getStatus(user);
	}

	@PUT
	@RolesAllowed("user")
	@Path("/accept")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserStatus putAccept() throws WebApplicationException {
		final User user = getCurrentUser();
		if (!user.getEvents().isEmpty()) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		}

		final EventAccepted event = new EventAccepted(user, Instant.now());
		userService.addSimpleEvent(event);
		return userService.getStatus(user);
	}

	@POST
	@RolesAllowed("user")
	@Path("/judgment")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserStatus postJudgment(Judgment judgment) throws WebApplicationException {
		final User user = getCurrentUser();
		if (user.getEvents().isEmpty()) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		}

		final EventJudgment event = new EventJudgment(user, Instant.now(), judgment);
		userService.addEvent(event);
		return userService.getStatus(user);
	}
}