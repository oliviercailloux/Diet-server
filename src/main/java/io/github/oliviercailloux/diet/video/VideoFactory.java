package io.github.oliviercailloux.diet.video;

import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
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
	public VideoAppendable getVideo(int fileId) {
		final TypedQuery<Video> q = em.createNamedQuery("get", Video.class);
		q.setParameter("fileId", fileId);
		return VideoAppendable.fromPersistent(em, q.getSingleResult());
	}

	private ImmutableSet<VideoAppendable> toAppendables(Collection<Video> result) {
		return result.stream().map(v -> VideoAppendable.fromPersistent(em, v)).collect(ImmutableSet.toImmutableSet());
	}

	@Transactional
	public ImmutableSet<VideoAppendable> getAll() {
		final TypedQuery<Video> q = em.createNamedQuery("all", Video.class);
		LOGGER.info("Querying for videos.");
		final List<Video> result = q.getResultList();
		LOGGER.info("Obtained result.");
		verify(!result.isEmpty());
		LOGGER.info("Result size {}.", result.size());
		return toAppendables(result);
	}

	@Transactional
	public ImmutableSet<VideoAppendable> getStarters() {
		final TypedQuery<Video> q = em.createNamedQuery("starters", Video.class);
		final List<Video> starters = q.getResultList();
		verify(!starters.isEmpty());
		return toAppendables(starters);
	}

	@Transactional
	public ImmutableSet<VideoAppendable> getReplies(Set<VideoAppendable> videos) {
		final TypedQuery<Video> q = em.createNamedQuery("replies", Video.class);
		q.setParameter("videos", videos.stream().map(VideoAppendable::video).collect(ImmutableSet.toImmutableSet()));
		final List<Video> replies = q.getResultList();
		return toAppendables(replies);
	}

}
