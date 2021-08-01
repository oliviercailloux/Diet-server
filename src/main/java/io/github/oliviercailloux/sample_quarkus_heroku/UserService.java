package io.github.oliviercailloux.sample_quarkus_heroku;

import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventAccepted;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.EventJudgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Judgment;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import io.quarkus.elytron.security.common.BcryptUtil;
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
	 * Adds a new user in the database
	 *
	 * @param username the user name
	 * @param password the unencrypted password (it will be encrypted with bcrypt)
	 * @param role     a role, no comma
	 */
	@Transactional
	public User add(String username, String password, String role) {
		User user = new User();
		user.setUsername(username);
		user.setPassword(BcryptUtil.bcryptHash(password));
		user.setRole(role);
		em.persist(user);
		return user;
	}

	@Transactional
	public User get(String username) {
		final TypedQuery<User> q = em.createNamedQuery("getUser", User.class);
		q.setParameter("username", username);
		final User user = q.getSingleResult();
		LOGGER.info("Got user {}, with events size {}.", username, user.getEvents().size());
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
