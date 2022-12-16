package io.github.oliviercailloux.diet;

import static com.google.common.base.Verify.verify;

import io.quarkus.runtime.Quarkus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

public class Main {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		Signal.handle(new Signal("TERM"), Main::handleSignal);
		Quarkus.run(args);
	}

	public static void handleSignal(Signal signal) {
		LOGGER.warn("Got signal {}.", signal);
		verify(signal.getName().equals("TERM"));
		System.exit(0);
	}
}