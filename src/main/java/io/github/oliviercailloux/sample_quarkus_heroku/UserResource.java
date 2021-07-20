package io.github.oliviercailloux.sample_quarkus_heroku;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("/users/me")
public class UserResource {

	@Inject
	EntityManager em;

	@Inject
	QueryHelper helper;

	@GET
	@RolesAllowed("user")
	@Path("/name")
	@Produces({ MediaType.TEXT_PLAIN + ";charset=utf-8" })
	public String name(@Context SecurityContext securityContext) {
		return securityContext.getUserPrincipal().getName();
	}

	@GET
	@RolesAllowed("user")
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON })
	public String status() {
		return "";
	}

	@GET
	@Path("/events")
	@Produces({ MediaType.TEXT_PLAIN + ";charset=utf-8" })
	public Event events() {
		return em.createQuery(helper.selectAll(Event.class)).getResultList().get(0);
	}
}