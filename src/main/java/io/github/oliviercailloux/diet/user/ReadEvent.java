package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.VerifyException;
import io.github.oliviercailloux.diet.video.Video;
import java.time.Instant;

public class ReadEvent {
	private final Event event;

	public static ReadEvent fromEvent(Event event) {
		if (event instanceof EventAccepted e) {
			return ReadEventAccepted.fromEvent(e);
		}
		if (event instanceof EventJudgment e) {
			return ReadEventJudgment.fromEvent(e);
		}
		if (event instanceof EventSeen e) {
			return ReadEventSeen.fromEvent(e);
		}
		throw new VerifyException();
	}

	protected ReadEvent(Event event) {
		this.event = checkNotNull(event);
	}

	Event underlyingEvent() {
		return event;
	}

	public Instant creation() {
		return event.getCreation();
	}

	public boolean isAccepted() {
		return event instanceof EventAccepted;
	}

	public boolean isSeen() {
		return event instanceof EventSeen;
	}

	public Video videoSeen() {
		return ((EventSeen) event).getVideo();
	}
}
