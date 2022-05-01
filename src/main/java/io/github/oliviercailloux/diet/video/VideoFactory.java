package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class VideoFactory {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoFactory.class);

	@Inject
	EntityManager em;

	@Transactional
	public Video getVideo(int fileId) {
		final TypedQuery<Video> q = em.createNamedQuery("get", Video.class);
		q.setParameter("fileId", fileId);
		return q.getSingleResult();
	}

	@Transactional
	public ImmutableSet<Video> getAll() {
		final TypedQuery<Video> q = em.createNamedQuery("all", Video.class);
		LOGGER.info("Querying for videos.");
		final List<Video> result = q.getResultList();
		LOGGER.info("Obtained result.");
		verify(!result.isEmpty());
		LOGGER.info("Result size {}.", result.size());
		return ImmutableSet.copyOf(result);
	}

	@Transactional
	public ImmutableSet<Video> getStarters() {
		final TypedQuery<Video> q = em.createNamedQuery("starters", Video.class);
		final List<Video> starters = q.getResultList();
		verify(!starters.isEmpty());
		return ImmutableSet.copyOf(starters);
	}

	@Transactional
	public ImmutableSet<Video> getReplies(Set<Video> videos) {
		final TypedQuery<Video> q = em.createNamedQuery("replies", Video.class);
		q.setParameter("videos", videos);
		final List<Video> replies = q.getResultList();
		return ImmutableSet.copyOf(replies);
	}

}
