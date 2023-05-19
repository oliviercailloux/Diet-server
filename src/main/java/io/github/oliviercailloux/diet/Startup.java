package io.github.oliviercailloux.diet;

import static com.google.common.base.Preconditions.checkState;

import io.github.oliviercailloux.diet.user.Judgment;
import io.github.oliviercailloux.diet.user.Login;
import io.github.oliviercailloux.diet.user.ReadEventJudgment;
import io.github.oliviercailloux.diet.user.UserFactory;
import io.github.oliviercailloux.diet.user.UserWithEvents;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import io.github.oliviercailloux.diet.video.Side;
import io.github.oliviercailloux.diet.video.VideoAppendable;
import io.github.oliviercailloux.diet.video.VideoFactory;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.net.URI;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Startup {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

	@ConfigProperty(name = "quarkus.http.port")
	int port;

	@ConfigProperty(name = "quarkus.datasource.jdbc.url")
	URI url;

	@Inject
	VideoFactory videoFactory;

	@Inject
	UserFactory userFactory;

	@Transactional
	public void loadAtStartup(@Observes StartupEvent evt) {
		LOGGER.info("Loading at startup, considering {}.", evt);
		checkState(url.getUserInfo() == null);
		LOGGER.info("Connected to {}.", url);
		loadVideos();
		loadUsers();
	}

	@SuppressWarnings("unused")
	@Transactional
	public void loadVideos() {
		final VideoAppendable v1 = videoFactory.add(1, "Climat et biodiversité", Side.VEGAN);
		final VideoAppendable v2 = videoFactory.add(2, "Santé vegan", Side.VEGAN);
		final VideoAppendable v3 = videoFactory.add(3, "Réduction pour écologie", Side.VEGAN);
		final VideoAppendable v4 = videoFactory.add(4, "Entente", Side.VEGAN);
		final VideoAppendable v5 = videoFactory.add(5, "Stratégie", Side.VEGAN);
		final VideoAppendable v6 = videoFactory.add(6, "Effort écologique", Side.VEGAN);
		final VideoAppendable v7 = videoFactory.add(7, "Consolidation", Side.VEGAN);
		final VideoAppendable v8 = videoFactory.add(8, "Durable, éthique et gout", Side.VEGAN);
		final VideoAppendable v9 = videoFactory.add(9, "Élevage moindre mal que transport", Side.MEAT);
		final VideoAppendable v10 = videoFactory.add(10, "Élevage encourage biodiversité", Side.MEAT);
		final VideoAppendable v11 = videoFactory.add(11, "Prairies bonnes pour GES", Side.MEAT);
		final VideoAppendable v12 = videoFactory.add(12, "Santé viande", Side.MEAT);
		final VideoAppendable v13 = videoFactory.add(13, "Viande pour ados", Side.MEAT);
		final VideoAppendable v14 = videoFactory.add(14, "Liberté de choix aux enfants", Side.MEAT);
		final VideoAppendable v15 = videoFactory.add(15, "B12 ou mauvais traitement", Side.MEAT);
		final VideoAppendable v16 = videoFactory.add(16, "Imposition de classe", Side.MEAT);
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