package io.github.oliviercailloux.diet;

import static com.google.common.base.Preconditions.checkState;

import io.github.oliviercailloux.diet.user.Judgment;
import io.github.oliviercailloux.diet.user.Login;
import io.github.oliviercailloux.diet.user.ReadEventJudgment;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserWithEvents;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.Side;
import io.github.oliviercailloux.diet.video.VideoFactory;
import io.github.oliviercailloux.diet.video.VideoWithCounters;
import io.quarkus.runtime.StartupEvent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Startup {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

	@Inject
	VideoFactory videoFactory;

	@Inject
	UserFactory userFactory;

	@Transactional
	public void loadAtStartup(@Observes StartupEvent evt) {
		LOGGER.info("Loading at startup, considering {}.", evt);
		loadVideos();
		loadUsers();
	}

	@SuppressWarnings("unused")
	@Transactional
	public void loadVideos() {
		final VideoWithCounters v1 = videoFactory.add(1, "Climat et biodiversité", Side.VEGAN);
		final VideoWithCounters v2 = videoFactory.add(2, "Santé vegan", Side.VEGAN);
		final VideoWithCounters v3 = videoFactory.add(3, "Réduction pour écologie", Side.VEGAN);
		final VideoWithCounters v4 = videoFactory.add(4, "Entente", Side.VEGAN);
		final VideoWithCounters v5 = videoFactory.add(5, "Stratégie", Side.VEGAN);
		final VideoWithCounters v6 = videoFactory.add(6, "Effort écologique", Side.VEGAN);
		final VideoWithCounters v7 = videoFactory.add(7, "Consolidation", Side.VEGAN);
		final VideoWithCounters v8 = videoFactory.add(8, "Durable, éthique et gout", Side.VEGAN);
		final VideoWithCounters v9 = videoFactory.add(9, "Élevage moindre mal que transport", Side.MEAT);
		final VideoWithCounters v10 = videoFactory.add(10, "Élevage encourage biodiversité", Side.MEAT);
		final VideoWithCounters v11 = videoFactory.add(11, "Prairies bonnes pour GES", Side.MEAT);
		final VideoWithCounters v12 = videoFactory.add(12, "Santé viande", Side.MEAT);
		final VideoWithCounters v13 = videoFactory.add(13, "Viande pour ados", Side.MEAT);
		final VideoWithCounters v14 = videoFactory.add(14, "Liberté de choix aux enfants", Side.MEAT);
		final VideoWithCounters v15 = videoFactory.add(15, "B12 ou mauvais traitement", Side.MEAT);
		final VideoWithCounters v16 = videoFactory.add(16, "Imposition de classe", Side.MEAT);
		v9.persistCounters(v1);
		v9.persistCounters(v3);
		v10.persistCounters(v1);
		v11.persistCounters(v1);
		v12.persistCounters(v2);
		v13.persistCounters(v2);
		v14.persistCounters(v2);
		v15.persistCounters(v2);
		final int newLatest = videoFactory.latestFileId();
		checkState(newLatest == 16);
	}

	@Transactional
	public void loadUsers() {
		userFactory.addAdmin(new Login("admin", "admin"));
		/*
		 * For the utf8-base64 equivalent to 'user0:user' for curl, use
		 * 'dXNlcjA=:dXNlcg=='.
		 */
		userFactory.addUser(new Login("user0", "user"));
		userFactory.addUser(new Login("élevé", "user"));
		{
			final UserWithEvents user = userFactory.addUser(new Login("inited", "user"));
			final Judgment judgment = new Judgment(1, 2);
			LOGGER.info("Adding {}.", judgment);
			user.persistEvent(ReadEventJudgment.now(judgment));
		}
		{
			final UserWithEvents user = userFactory.addUser(new Login("seen", "user"));
			final Judgment judgment = new Judgment(3, 1);
			user.persistEvent(ReadEventJudgment.now(judgment));
			user.persistEvent(ReadEventSeen.now(videoFactory.getVideo(3)));
		}
	}
}