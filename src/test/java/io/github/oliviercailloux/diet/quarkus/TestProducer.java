package io.github.oliviercailloux.diet.quarkus;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TestProducer {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(TestProducer.class);

	@Produces
	@ApplicationScoped
	public Client getClient() {
		LOGGER.info("Producing client.");
		return ClientBuilder.newClient();
	}

	@Produces
	@ApplicationScoped
	@AdminClient
	public Client authenticatingClient() {
		LOGGER.info("Producing authenticating client.");
		return ClientBuilder.newClient().register(Authenticator.fromEnvironment());
	}

	public void close(@Disposes Client client) {
		LOGGER.info("Disposing of JAX-RS client.");
		client.close();
	}
}
