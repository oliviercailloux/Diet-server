package io.github.oliviercailloux.diet;

import io.github.oliviercailloux.diet.user.ReadEventSeen;
import io.github.oliviercailloux.diet.user.UserAppendable;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserStatus;
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
	public UserStatus putSeen(@PathParam("fileId") int fileId) throws WebApplicationException {
		final UserAppendable user = userFactory.getAppendable(getCurrentUsername());
		user.persistEvent(ReadEventSeen.now(videoFactory.getVideo(fileId)));
		return user.status();
	}
}