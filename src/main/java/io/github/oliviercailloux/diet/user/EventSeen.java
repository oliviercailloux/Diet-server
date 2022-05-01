package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import io.github.oliviercailloux.diet.video.Video;
import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class EventSeen extends Event {

	@ManyToOne(fetch = FetchType.EAGER)
	@NotNull
	private Video video;

	EventSeen() {
		/* For JPA. */
	}

	public EventSeen(Instant creation, Video video) {
		super(creation);
		this.video = checkNotNull(video);
	}

	public EventSeen(User user, Instant creation, Video video) {
		super(user, creation);
		this.video = checkNotNull(video);
	}

	public Video getVideo() {
		return video;
	}

	@Override
	public String toString() {
		final ToStringHelper stringHelper = MoreObjects.toStringHelper(this);
		populate(stringHelper);
		return stringHelper.add("Video", video).toString();
	}

}