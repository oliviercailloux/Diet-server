package io.github.oliviercailloux.sample_quarkus_heroku;

import io.github.oliviercailloux.sample_quarkus_heroku.dao.Base64;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventAccepted;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventJudgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Judgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class UserService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Inject
	EntityManager em;

	/**
	 * Adds a new user in the database with role admin
	 *
	 * @param username the user name
	 * @param password the unencrypted password (it will be encrypted with bcrypt)
	 * @param role     a role, no comma
	 */
	@Transactional
	public User addAdmin(String username, String password) {
		return add(username, password, "admin");
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param username the user name
	 * @param password the unencrypted password (it will be encrypted with bcrypt)
	 * @param role     a role, no comma
	 */
	@Transactional
	public User addUser(String username, String password) {
		return add(username, password, "user");
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param username the user name
	 * @param password the unencrypted password (it will be encrypted with bcrypt)
	 * @param role     a role, no comma
	 */
	@Transactional
	private User add(String username, String password, String role) {
		User user = new User(username, password, role);
		em.persist(user);
		return user;
	}

	@Transactional
	public User get(String unencodedUsername) {
		final Base64 base64 = Base64.from(unencodedUsername);
		LOGGER.info("Searching for unencoded {}, thus encoded {}.", unencodedUsername, base64);
		return get(base64);
	}

	public User get(Base64 base64Username) {
		final TypedQuery<User> q = em.createNamedQuery("getBase64User", User.class);
		q.setParameter("username", base64Username.getRawBase64String());
		final User user = q.getSingleResult();
		LOGGER.info("Got user encoded {}, with events size {}.", base64Username, user.getEvents().size());
		return user;
	}

	public void addEvent(EventAccepted event) {
		final User user = event.getUser();
		user.addEvent(event);
		em.persist(event);
		em.persist(user);
	}

	public void addEvent(EventJudgment event) {
		final User user = event.getUser();
		user.addEvent(event);
		final Judgment judgment = event.getJudgment();
		em.persist(judgment);
		LOGGER.info("Persisting {}.", judgment);
		em.persist(event);
		em.persist(user);
	}

}
