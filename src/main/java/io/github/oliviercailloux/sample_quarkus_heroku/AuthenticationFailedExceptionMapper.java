package io.github.oliviercailloux.sample_quarkus_heroku;

import io.quarkus.security.AuthenticationFailedException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thanks to https://stackoverflow.com/a/68439448.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFailedExceptionMapper implements ExceptionMapper<AuthenticationFailedException> {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFailedExceptionMapper.class);

	@Override
	public Response toResponse(AuthenticationFailedException exception) {
		LOGGER.info("Authentication failed: {}.", exception.getCause().getMessage());
		return Response.status(401).header("WWW-Authenticate", "Basic realm=\"Diet\"").entity("Invalid password")
				.build();
	}
}