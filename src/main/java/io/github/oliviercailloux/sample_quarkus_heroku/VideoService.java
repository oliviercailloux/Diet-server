package io.github.oliviercailloux.sample_quarkus_heroku;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.sample_quarkus_heroku.entity.Video;
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
public class VideoService {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoService.class);

	@Inject
	EntityManager em;

	@Transactional
	public ImmutableSet<Video> getStarters() {
		final TypedQuery<Video> q = em.createNamedQuery("starters", Video.class);
		final List<Video> replies = q.getResultList();
		return ImmutableSet.copyOf(replies);
	}

	@Transactional
	public ImmutableSet<Video> getReplies(Set<Video> videos) {
		final TypedQuery<Video> q = em.createNamedQuery("replies", Video.class);
		q.setParameter("videos", videos);
		final List<Video> replies = q.getResultList();
		return ImmutableSet.copyOf(replies);
	}

}
