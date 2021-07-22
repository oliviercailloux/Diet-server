package io.github.oliviercailloux.sample_quarkus_heroku;

import io.github.oliviercailloux.sample_quarkus_heroku.entity.Event;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventAccepted;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import io.quarkus.runtime.StartupEvent;
import java.time.Instant;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Singleton
public class Startup {
	@Inject
	EntityManager em;

	@Inject
	UserService userService;

	@Transactional
	public void loadUsers(@SuppressWarnings("unused") @Observes StartupEvent evt) {
		userService.add("admin", "admin", "admin");
		final User user = userService.add("user", "user", "user");

		final Event event = new EventAccepted(user, Instant.now());
		userService.addEvent(event);
	}
}