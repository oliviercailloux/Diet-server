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
	public VideoWithCounters add(int fileId, String description, Side side) {
		final VideoEntity v = new VideoEntity(fileId, description, side);
		em.persist(v);
		return VideoWithCounters.fromPersistent(em, v);
	}

	@Transactional
	public Video getVideo(int fileId) {
		final TypedQuery<VideoEntity> q = em.createNamedQuery("get", VideoEntity.class);
		q.setParameter("fileId", fileId);
		final VideoEntity entity = q.getSingleResult();
		/*
		 * Apprently, can sometimes skip the query and return a cached entity; thus it
		 * might have counters.
		 */
//		verify(!entity.hasCounters());
		return Video.fromPersistent(entity);
	}

	private ImmutableSet<Video> toVideos(Collection<VideoEntity> result) {
		return result.stream().map(v -> Video.fromPersistent(v)).collect(ImmutableSet.toImmutableSet());
	}

	private ImmutableSet<VideoWithCounters> toAppendables(Collection<VideoEntity> result) {
		return result.stream().map(v -> VideoWithCounters.fromPersistent(em, v)).collect(ImmutableSet.toImmutableSet());
	}

	@Transactional
	public ImmutableSet<VideoWithCounters> getAll() {
		final TypedQuery<VideoEntity> q = em.createNamedQuery("all", VideoEntity.class);
		LOGGER.info("Querying for videos.");
		final List<VideoEntity> result = q.getResultList();
		LOGGER.info("Obtained result.");
		verify(!result.isEmpty());
		LOGGER.info("Result size {}.", result.size());
		return toAppendables(result);
	}

	@Transactional
	public ImmutableSet<Video> getAllSimple() {
		final TypedQuery<VideoEntity> q = em.createNamedQuery("all", VideoEntity.class);
		LOGGER.info("Querying for videos.");
		final List<VideoEntity> result = q.getResultList();
		LOGGER.info("Obtained result.");
		verify(!result.isEmpty());
		LOGGER.info("Result size {}.", result.size());
		return toVideos(result);
	}

	@Transactional
	public ImmutableSet<Video> getStarters() {
		final TypedQuery<VideoEntity> q = em.createNamedQuery("starters", VideoEntity.class);
		final List<VideoEntity> starters = q.getResultList();
		verify(!starters.isEmpty());
		return toVideos(starters);
	}

	@Transactional
	public ImmutableSet<Video> getReplies(Set<Video> videos) {
		final TypedQuery<VideoEntity> q = em.createNamedQuery("replies", VideoEntity.class);
		q.setParameter("videos", videos.stream().map(Video::video).collect(ImmutableSet.toImmutableSet()));
		final List<VideoEntity> replies = q.getResultList();
		return toVideos(replies);
	}

	public int latestFileId() {
		final TypedQuery<Integer> q2 = em.createNamedQuery("latest file id", Integer.class);
		final int newLatest = q2.getSingleResult();
		return newLatest;
	}

}
