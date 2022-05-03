package io.github.oliviercailloux.diet.user;

import io.github.oliviercailloux.diet.video.VideoFactory;
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
	VideoFactory videoFactory;

	private User getUser(String username) {
		final TypedQuery<User> q = em.createNamedQuery("getUser", User.class);
		q.setParameter("username", username);
		return q.getSingleResult();
	}

	private User getUserWithoutEvents(String username) {
		final TypedQuery<User> q = em.createNamedQuery("getUserWithoutEvents", User.class);
		q.setParameter("username", username);
		return q.getSingleResult();
	}

	@Transactional
	private UserPersistentWithEvents getOld(String username) {
		final User user = getUser(username);
		return UserPersistentWithEvents.fromExistingWithEvents(em, videoFactory, user);
	}

	public RawUser getWithoutEvents(String username) {
		final User user = getUserWithoutEvents(username);
		return UserPersistent.persistent(user);
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param login with the unencrypted password (it will be encrypted with bcrypt)
	 */
	@Transactional
	public RawUser addAdmin(Login login) {
		final User user = new User(login, "admin");
		em.persist(user);
		return UserPersistent.persistent(user);
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param login with the unencrypted password (it will be encrypted with bcrypt)
	 */
	@Transactional
	public UserPersistentWithEvents addUser(Login login) {
		final User user = new User(login, "user");
		final EventAccepted event = user.setAccepted();
		em.persist(user);
		em.persist(event);
		return UserPersistentWithEvents.fromExistingWithEvents(em, videoFactory, user);
	}

	public UserPersistentWithEvents getAppendable(String username) {
		final User user = getUser(username);
		return UserPersistentWithEvents.fromExistingWithEvents(em, videoFactory, user);
	}

}
