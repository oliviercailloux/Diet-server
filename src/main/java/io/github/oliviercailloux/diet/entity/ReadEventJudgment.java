package io.github.oliviercailloux.diet.entity;

import java.time.Instant;

public class ReadEventJudgment extends ReadEvent {
	public static ReadEventJudgment now(Judgment judgment) {
		return new ReadEventJudgment(new EventJudgment(Instant.now(), judgment));
	}

	public static ReadEvent fromEvent(EventJudgment event) {
		return new ReadEventJudgment(event);
	}

	private ReadEventJudgment(EventJudgment event) {
		super(event);
	}
}
