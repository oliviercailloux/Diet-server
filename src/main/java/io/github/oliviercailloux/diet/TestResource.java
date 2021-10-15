package io.github.oliviercailloux.diet;

import javax.annotation.security.PermitAll;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class TestResource {

	@GET
	@PermitAll
	@Path("/test")
	@Produces({ MediaType.APPLICATION_JSON })
	@Transactional
	public int test() throws WebApplicationException {
		return 0;
	}
}