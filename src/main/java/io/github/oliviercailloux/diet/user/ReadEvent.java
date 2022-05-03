package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.VerifyException;
import io.github.oliviercailloux.diet.video.ReadEventSeen;
import java.time.Instant;
import java.util.Comparator;
import javax.persistence.EntityManager;

public class ReadEvent {
	public static final Comparator<ReadEvent> COMPARATOR = Comparator.comparing(ReadEvent::creation)
			.thenComparingInt(ReadEvent::hashCode);

	private final Event event;

	public static ReadEvent fromEvent(EntityManager em, Event event) {
		if (event instanceof EventAccepted e) {
			return ReadEventAccepted.fromEvent(e);
		}
		if (event instanceof EventJudgment e) {
			return ReadEventJudgment.fromEvent(e);
		}
		if (ReadEventSeen.accept(event)) {
			return ReadEventSeen.fromEventSeen(em, event);
		}
		throw new VerifyException();
	}

	protected ReadEvent(Event event) {
		this.event = checkNotNull(event);
	}

	protected Event underlyingEvent() {
		return event;
	}

	public Instant creation() {
		return event.getCreation();
	}
}
