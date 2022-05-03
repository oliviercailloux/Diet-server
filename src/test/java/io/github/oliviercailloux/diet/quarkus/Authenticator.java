package io.github.oliviercailloux.diet.quarkus;

import io.github.oliviercailloux.jaris.collections.ImmutableCompleteMap;
import io.github.oliviercailloux.jaris.credentials.CredentialsReader;
import io.github.oliviercailloux.jaris.credentials.CredentialsReader.ClassicalCredentials;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;

public class Authenticator implements ClientRequestFilter {

	public static Authenticator fromEnvironment() {
		final ImmutableCompleteMap<ClassicalCredentials, String> credentials = CredentialsReader.classicalReader()
				.getCredentials();
		return new Authenticator(credentials);
	}

	private final String user;
	private final String password;

	public Authenticator(ImmutableCompleteMap<ClassicalCredentials, String> credentials) {
		this(credentials.get(ClassicalCredentials.API_USERNAME), credentials.get(ClassicalCredentials.API_PASSWORD));
	}

	public Authenticator(String user, String password) {
		this.user = user;
		this.password = password;
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, getBasicAuthentication());
	}

	private String getBasicAuthentication() {
		final String auth = user + ":" + password;
		final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		return "Basic " + new String(encodedAuth, StandardCharsets.US_ASCII);
	}
}