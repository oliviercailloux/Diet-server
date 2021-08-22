package io.github.oliviercailloux.sample_quarkus_heroku;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.github.oliviercailloux.sample_quarkus_heroku.dao.Base64;
import io.github.oliviercailloux.sample_quarkus_heroku.dao.UserStatus;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventAccepted;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventJudgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Judgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Video;
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
	@Inject
	VideoService videoService;

	private Base64 getCurrentUsername() {
		final String username = securityContext.getUserPrincipal().getName();
		return Base64.alreadyBase64(username);
	}

	private User getCurrentUser() {
		final Base64 username = getCurrentUsername();
		return userService.get(username);
	}

	@Transactional
	private UserStatus getStatus(User user) {
		final ImmutableSet<Video> seen = user.getSeen();
		final ImmutableSet<Video> replies = videoService.getReplies(seen);
		final ImmutableSet<Video> toSee = Sets.difference(Sets.union(videoService.getStarters(), replies), seen)
				.immutableCopy();
		return new UserStatus(user, toSee.asList());
	}

	@GET
	@RolesAllowed("user")
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserStatus status() {
		final User user = getCurrentUser();
		return getStatus(user);
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
		userService.addEvent(event);
		return getStatus(user);
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
		userService.addEvent(event);
		return user;
	}
}