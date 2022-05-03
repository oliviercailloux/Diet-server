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
	VideoFactory videoFactory;

	private UserEntity getUserWithoutEvents(String username) {
		final TypedQuery<UserEntity> q = em.createNamedQuery("getUserWithoutEvents", UserEntity.class);
		q.setParameter("username", username);
		return q.getSingleResult();
	}

	private UserEntity getUserWithEvents(String username) {
		final TypedQuery<UserEntity> q = em.createNamedQuery("getUserWithEvents", UserEntity.class);
		q.setParameter("username", username);
		return q.getSingleResult();
	}

	public RawUser getWithoutEvents(String username) {
		final UserEntity user = getUserWithoutEvents(username);
		return User.persistent(user);
	}

	@Transactional
	public UserWithEvents getWithEvents(String username) {
		final UserEntity user = getUserWithEvents(username);
		return UserWithEvents.fromExistingWithEvents(em, videoFactory, user);
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param login with the unencrypted password (it will be encrypted with bcrypt)
	 */
	@Transactional
	public RawUser addAdmin(Login login) {
		final UserEntity user = new UserEntity(login, "admin");
		em.persist(user);
		return User.persistent(user);
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param login with the unencrypted password (it will be encrypted with bcrypt)
	 */
	@Transactional
	public UserWithEvents addUser(Login login) {
		return addUser(login, Instant.now());
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param login with the unencrypted password (it will be encrypted with bcrypt)
	 */
	@Transactional
	public UserWithEvents addUser(Login login, Instant acceptationTime) {
		final UserEntity user = new UserEntity(login, "user");
		final EventAccepted event = user.setAccepted(acceptationTime);
		LOGGER.info("Persisting {}.", user);
		em.persist(user);
		em.persist(event);
		return UserWithEvents.fromExistingWithEvents(em, videoFactory, user);
	}

	public UserWithEvents getAppendable(String username) {
		final UserEntity user = getUserWithEvents(username);
		return UserWithEvents.fromExistingWithEvents(em, videoFactory, user);
	}

}
