package io.github.oliviercailloux.diet;

import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserWithEvents;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.VideoFactory;
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
import javax.ws.rs.core.SecurityContext;

@Path("/video")
public class VideoResource {

	@Context
	SecurityContext securityContext;

	@Inject
	UserFactory userFactory;
	@Inject
	VideoFactory videoFactory;

	private String getCurrentUsername() {
		return securityContext.getUserPrincipal().getName();
	}

	@PUT
	@RolesAllowed("user")
	@Path("/{fileId}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public UserWithEvents putSeen(@PathParam("fileId") int fileId) throws WebApplicationException {
		final UserWithEvents user = userFactory.getWithEvents(getCurrentUsername());
		user.persistEvent(ReadEventSeen.now(videoFactory.getVideo(fileId)));
		return user;
	}
}