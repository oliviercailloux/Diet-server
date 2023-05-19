package io.github.oliviercailloux.diet.resources;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.draw.Drawer;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserWithEvents;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.VideoAppendable;
import io.github.oliviercailloux.diet.video.VideoFactory;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.w3c.dom.Document;

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

	@GET
	@PermitAll
	@Path("/svg")
	@Produces({ MediaType.APPLICATION_SVG_XML })
	@Transactional
	public Document svg() {
		final ImmutableSet<VideoAppendable> vs = videoFactory.getAll();
		return Drawer.drawer(vs).svg();
	}

}