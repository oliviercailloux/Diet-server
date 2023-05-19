package io.github.oliviercailloux.diet.resources;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.draw.Drawer;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserWithEvents;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.VideoAppendable;
import io.github.oliviercailloux.diet.video.VideoFactory;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
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