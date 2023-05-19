package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Verify.verify;

import io.github.oliviercailloux.diet.video.VideoFactory;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.Instant;
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
		final UserEntity user = q.getSingleResult();
		return user;
	}

	private UserEntity getUserWithEvents(String username) {
		final TypedQuery<UserEntity> q = em.createNamedQuery("getUserWithEvents", UserEntity.class);
		q.setParameter("username", username);
		final UserEntity user = q.getSingleResult();
		verify(user.hasEvents());
		return user;
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
		em.persist(user);
		em.persist(event);
		return UserWithEvents.fromExistingWithEvents(em, videoFactory, user);
	}

}
