package io.github.oliviercailloux.sample_quarkus_heroku;

import io.github.oliviercailloux.sample_quarkus_heroku.entity.User;
import io.quarkus.elytron.security.common.BcryptUtil;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@RequestScoped
@NamedQuery(name = "getUser", query = "SELECT u FROM User u WHERE username = :username")
public class UserService {
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
		return q.getSingleResult();
	}

}
