package io.github.oliviercailloux.sample_quarkus_heroku;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

@Path("/users")
public class UserResource {

	@GET
	@RolesAllowed("user")
	@Path("/me")
	public String me(@Context SecurityContext securityContext) {
		return securityContext.getUserPrincipal().getName();
	}
}