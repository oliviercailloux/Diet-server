package io.github.oliviercailloux.diet;

import static com.google.common.base.Verify.verify;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

@QuarkusMain
public class Main {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		LOGGER.info("Installing signal handler.");
		Signal.handle(new Signal("TERM"), Main::handleSignal);
		Quarkus.run(args);
	}

	public static void handleSignal(Signal signal) {
		LOGGER.info("Got TERM signal {}.", signal, "exitting with zero");
		verify(signal.getNumber() == 15);
		System.exit(0);
	}
}