package io.github.oliviercailloux.diet.quarkus;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.diet.user.Login;
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

	private final Login login;

	public Authenticator(Login login) {
		this.login = checkNotNull(login);
		checkArgument(!login.getUsername().contains(":"));
	}

	public Authenticator(String user, String password) {
		this(new Login(user, password));
	}

	public Authenticator(ImmutableCompleteMap<ClassicalCredentials, String> credentials) {
		this(credentials.get(ClassicalCredentials.API_USERNAME), credentials.get(ClassicalCredentials.API_PASSWORD));
	}

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, getBasicAuthentication());
	}

	private String getBasicAuthentication() {
		final String auth = login.getUsername() + ":" + login.getPassword();
		final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		return "Basic " + new String(encodedAuth, StandardCharsets.US_ASCII);
	}
}