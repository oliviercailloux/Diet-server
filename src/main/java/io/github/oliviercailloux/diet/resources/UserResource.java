package io.github.oliviercailloux.diet.resources;

import io.github.oliviercailloux.diet.user.Judgment;
import io.github.oliviercailloux.diet.user.Login;
import io.github.oliviercailloux.diet.user.ReadEventJudgment;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserWithEvents;
import io.github.oliviercailloux.diet.utils.BasicUsername;
import io.github.oliviercailloux.diet.video.VideoFactory;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/me")
public class UserResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

	@Context
	SecurityContext securityContext;

	@Inject
	UserFactory userFactory;

	@Inject
	VideoFactory videoService;

	@Inject
	EntityManager em;

	private String getCurrentUsername() {
		return securityContext.getUserPrincipal().getName();
	}

	@GET
	@RolesAllowed("user")
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserWithEvents status() {
		return userFactory.getWithEvents(getCurrentUsername());
	}

	/**
	 * Idempotent: when body changes, new user is created; otherwise, does not
	 * create new user.
	 *
	 * This should return a HTTP created, I suppose, and be based at the root.
	 */
	@PUT
	@PermitAll
	@Path("/create-accept")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserWithEvents createAcceptingUser(@NotNull @BasicUsername Login login) {
		LOGGER.info("Creating {}.", login);
		return userFactory.addUser(login);
	}

	@POST
	@RolesAllowed("user")
	@Path("/judgment")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserWithEvents postJudgment(Judgment judgment) {
		final UserWithEvents user = userFactory.getWithEvents(getCurrentUsername());
		final ReadEventJudgment event = ReadEventJudgment.now(judgment);
		user.persistEvent(event);
		return user;
	}

}