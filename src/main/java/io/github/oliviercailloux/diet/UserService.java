package io.github.oliviercailloux.diet;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.github.oliviercailloux.diet.dao.Base64;
import io.github.oliviercailloux.diet.dao.Login;
import io.github.oliviercailloux.diet.dao.UserStatus;
import io.github.oliviercailloux.diet.entity.Event;
import io.github.oliviercailloux.diet.entity.EventJudgment;
import io.github.oliviercailloux.diet.entity.Judgment;
import io.github.oliviercailloux.diet.entity.User;
import io.github.oliviercailloux.diet.entity.Video;
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

	@Inject
	VideoService videoService;

	/**
	 * Adds a new user in the database with role admin
	 */
	@Transactional
	public User addAdmin(Login login) {
		return add(login, "admin");
	}

	/**
	 * Adds a new user in the database with role user
	 */
	@Transactional
	public User addUser(Login login) {
		return add(login, "user");
	}

	/**
	 * Adds a new user in the database with role user
	 *
	 * @param login with the unencrypted password (it will be encrypted with bcrypt)
	 * @param role  a role, no comma
	 */
	@Transactional
	private User add(Login login, String role) {
		User user = new User(login, role);
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
		LOGGER.info("Got user encoded {}.", base64Username);
		LOGGER.info("Got user encoded {}, with events size {}.", base64Username, user.getEvents().size());
		return user;
	}

	public User getWithoutEvents(Base64 base64Username) {
		final TypedQuery<User> q = em.createNamedQuery("getBase64UserWithoutEvents", User.class);
		q.setParameter("username", base64Username.getRawBase64String());
		return q.getSingleResult();
	}

	public void addSimpleEvent(Event event) {
		final User user = event.getUser();
		user.addEvent(event);
		em.persist(event);
		em.persist(user);
	}

	public void addEvent(EventJudgment event) {
		final User user = event.getUser();
		user.addEvent(event);
		final Judgment judgment = event.getJudgment();
		LOGGER.info("Persisting {}.", judgment);
		em.persist(judgment);
		em.persist(event);
		em.persist(user);
	}

	@Transactional
	public UserStatus getStatus(User user) {
		final ImmutableSet<Video> seen = user.getSeen();
		final ImmutableSet<Video> all = videoService.getAll();
		final ImmutableSet<Video> toSee = Sets.difference(all, seen).immutableCopy();
		return new UserStatus(user, toSee.asList());
	}

	@Transactional
	public UserStatus getStatusWithImmediateRepliesOnly(User user) {
		final ImmutableSet<Video> seen = user.getSeen();
		final ImmutableSet<Video> replies = videoService.getReplies(seen);
		final ImmutableSet<Video> toSee = Sets.difference(Sets.union(videoService.getStarters(), replies), seen)
				.immutableCopy();
		return new UserStatus(user, toSee.asList());
	}

}
