package io.github.oliviercailloux.sample_quarkus_heroku;

import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/users")
@RequestScoped
public class UserResource {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

	@Inject
	UserService service;

	@Inject
	SecurityContext securityContext;

	@GET
	@RolesAllowed("user")
	@Path("/me")
	@Produces({ MediaType.APPLICATION_JSON })
	public User me() {
		LOGGER.info("Context: {}.", securityContext);
		return service.getCurrentUser();
	}

}