package io.github.oliviercailloux.diet;

import io.github.oliviercailloux.diet.dao.Base64String;
import io.github.oliviercailloux.diet.dao.UserStatus;
import io.github.oliviercailloux.diet.entity.EventSeen;
import io.github.oliviercailloux.diet.entity.User;
import java.time.Instant;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/video")
public class VideoResource {

	@Context
	SecurityContext securityContext;

	@Inject
	UserService userService;
	@Inject
	VideoService videoService;

	private Base64String getCurrentUsername() {
		final String username = securityContext.getUserPrincipal().getName();
		return Base64String.alreadyBase64(username);
	}

	private User getCurrentUser() {
		final Base64String username = getCurrentUsername();
		return userService.get(username);
	}

	@PUT
	@RolesAllowed("user")
	@Path("/{fileId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserStatus putSeen(@PathParam("fileId") int fileId) throws WebApplicationException {
		final User user = getCurrentUser();
		if (user.getEvents().isEmpty()) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		}

		final EventSeen event = new EventSeen(user, Instant.now(), videoService.getVideo(fileId));
		userService.addSimpleEvent(event);
		return userService.getStatus(user);
	}
}