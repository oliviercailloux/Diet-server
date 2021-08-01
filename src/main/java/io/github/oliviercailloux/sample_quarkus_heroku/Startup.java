package io.github.oliviercailloux.sample_quarkus_heroku;

import io.github.oliviercailloux.sample_quarkus_heroku.entity.ArguerAttack;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventAccepted;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventJudgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Judgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Video;
import io.quarkus.runtime.StartupEvent;
import java.time.Instant;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Startup {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

	@Inject
	EntityManager em;

	@Inject
	UserService userService;

	@Transactional
	public void loadUsers(@SuppressWarnings("unused") @Observes StartupEvent evt) {
		userService.add("admin", "admin", "admin");
		final User user = userService.add("user", "user", "user");

		userService.addEvent(new EventAccepted(user, Instant.now()));
		final EventJudgment je = new EventJudgment(user, Instant.now(), new Judgment(1, 2));
		LOGGER.info("Adding {}.", je);
		userService.addEvent(je);
	}

	@Transactional
	public void loadVideos(@SuppressWarnings("unused") @Observes StartupEvent evt) {
		final Video v1 = new Video("descr 1");
		final Video v2 = new Video("descr 2");
		final ArguerAttack attack = v2.addCounters(v1);
		em.persist(v1);
		em.persist(v2);
		em.persist(attack);
	}
}