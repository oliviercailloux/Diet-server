package io.github.oliviercailloux.sample_quarkus_heroku;

import io.quarkus.elytron.security.common.BcryptUtil;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@RequestScoped
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

}
