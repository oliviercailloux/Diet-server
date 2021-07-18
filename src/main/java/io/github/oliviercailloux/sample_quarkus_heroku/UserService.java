package io.github.oliviercailloux.sample_quarkus_heroku;

import io.quarkus.elytron.security.common.BcryptUtil;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@RequestScoped
public class UserService {
	@Inject
	EntityManager em;

	/**
	 * Adds a new user in the database
	 *
	 * @param username the user name
	 * @param password the unencrypted password (it will be encrypted with bcrypt)
	 * @param role     the comma-separated roles
	 */
	public void add(String username, String password, String role) {
		User user = new User();
		user.username = username;
		user.password = BcryptUtil.bcryptHash(password);
		user.role = role;
		em.persist(user);
	}

}
