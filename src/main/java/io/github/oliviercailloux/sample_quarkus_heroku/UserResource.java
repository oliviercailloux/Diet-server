package io.github.oliviercailloux.sample_quarkus_heroku;

import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/users")
public class UserResource {

	@Inject
	UserService service;

	@GET
	@RolesAllowed("user")
	@Path("/me")
	@Produces({ MediaType.APPLICATION_JSON })
	public User me(@Context SecurityContext securityContext) {
		final String username = securityContext.getUserPrincipal().getName();
		return service.get(username);
	}
}