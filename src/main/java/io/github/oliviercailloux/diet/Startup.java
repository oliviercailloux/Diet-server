package io.github.oliviercailloux.diet;

import static com.google.common.base.Preconditions.checkState;

import io.github.oliviercailloux.diet.entity.EventAccepted;
import io.github.oliviercailloux.diet.entity.EventJudgment;
import io.github.oliviercailloux.diet.entity.Judgment;
import io.github.oliviercailloux.diet.entity.User;
import io.github.oliviercailloux.diet.entity.Video;
import io.quarkus.runtime.StartupEvent;
import java.time.Instant;
import java.util.Optional;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
		userService.addAdmin("admin", "admin");
		userService.addUser("user0", "user");
		{
			final User userAccepted = userService.addUser("accepted", "user");
			userService.addEvent(new EventAccepted(userAccepted, Instant.now()));
		}
		{
			final User userInited = userService.addUser("inited", "user");
			userService.addEvent(new EventAccepted(userInited, Instant.now()));
			final EventJudgment je = new EventJudgment(userInited, Instant.now(), new Judgment(1, 2));
			LOGGER.info("Adding {}.", je);
			userService.addEvent(je);
		}
	}

	@Transactional
	public void loadVideos(@SuppressWarnings("unused") @Observes StartupEvent evt) {
		final TypedQuery<Integer> q = em.createNamedQuery("latest file id", Integer.class);
		final Optional<Integer> initialLatest = Optional.ofNullable(q.getSingleResult());
		checkState(initialLatest.isEmpty());
		final Video v1 = new Video(1, "Climat et biodiversité");
		final Video v2 = new Video(2, "Santé vegan");
		final Video v3 = new Video(3, "Réduction pour écologie");
		final Video v4 = new Video(4, "Entente");
		final Video v5 = new Video(5, "Stratégie");
		final Video v6 = new Video(6, "Effort écologique");
		final Video v7 = new Video(7, "Consolidation");
		final Video v8 = new Video(8, "Durable, éthique et gout");
		final Video v9 = new Video(9, "Élevage moindre mal que transport");
		final Video v10 = new Video(10, "Élevage encourage biodiversité");
		final Video v11 = new Video(11, "Prairies bonnes pour GES");
		final Video v12 = new Video(12, "Santé viande");
		final Video v13 = new Video(13, "Viande pour ados");
		final Video v14 = new Video(14, "Liberté de choix aux enfants");
		final Video v15 = new Video(15, "B12 ou mauvais traitement");
		final Video v16 = new Video(16, "Imposition de classe");
		em.persist(v1);
		em.persist(v2);
		em.persist(v3);
		em.persist(v4);
		em.persist(v5);
		em.persist(v6);
		em.persist(v7);
		em.persist(v8);
		em.persist(v9);
		em.persist(v10);
		em.persist(v11);
		em.persist(v12);
		em.persist(v13);
		em.persist(v14);
		em.persist(v15);
		em.persist(v16);
		em.persist(v9.addCounters(v1));
		em.persist(v9.addCounters(v3));
		em.persist(v10.addCounters(v1));
		em.persist(v11.addCounters(v1));
		em.persist(v12.addCounters(v2));
		em.persist(v13.addCounters(v2));
		em.persist(v14.addCounters(v2));
		em.persist(v15.addCounters(v2));
		final TypedQuery<Integer> q2 = em.createNamedQuery("latest file id", Integer.class);
		final int newLatest = q2.getSingleResult();
		checkState(newLatest == 16);
	}
}