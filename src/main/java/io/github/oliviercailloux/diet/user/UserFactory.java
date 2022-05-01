package io.github.oliviercailloux.diet.user;

import io.github.oliviercailloux.diet.video.VideoFactory;
import java.time.Instant;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class UserFactory {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserFactory.class);

	@Inject
	EntityManager em;

	@Inject
	VideoFactory videoService;

	private User get(String username) {
		final TypedQuery<User> q = em.createNamedQuery("getUser", User.class);
		q.setParameter("username", username);
		return q.getSingleResult();
	}

	@SuppressWarnings("unused")
	private User getWithoutEvents(String username) {
		final TypedQuery<User> q = em.createNamedQuery("getUserWithoutEvents", User.class);
		q.setParameter("username", username);
		return q.getSingleResult();
	}

	@Transactional
	public UserStatus getStatus(String username) {
		final User user = get(username);
		return UserStatus.fromExistingUser(user, videoService);
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param login with the unencrypted password (it will be encrypted with bcrypt)
	 */
	@Transactional
	public void addAdmin(Login login) {
		final User user = new User(login, "admin");
		em.persist(user);
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param login with the unencrypted password (it will be encrypted with bcrypt)
	 */
	@Transactional
	public UserAppendable addUser(Login login) {
		final User user = new User(login, "user");
		final EventAccepted event = new EventAccepted(user, Instant.now());
		user.events().add(event);
		em.persist(user);
		em.persist(event);
		return UserAppendable.fromExistingWithEvents(em, videoService, user);
	}

	public UserAppendable getAppendable(String username) {
		final User user = get(username);
		return UserAppendable.fromExistingWithEvents(em, videoService, user);
	}

}
