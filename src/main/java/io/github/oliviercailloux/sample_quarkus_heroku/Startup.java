package io.github.oliviercailloux.sample_quarkus_heroku;

import io.quarkus.runtime.StartupEvent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

@Singleton
public class Startup {
	@Inject
	UserService userService;

	@Transactional
	public void loadUsers(@Observes StartupEvent evt) {
		userService.add("admin", "admin", "admin");
		userService.add("user", "user", "user");
	}
}