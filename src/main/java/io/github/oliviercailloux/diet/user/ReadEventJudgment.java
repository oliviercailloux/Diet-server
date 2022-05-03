package io.github.oliviercailloux.diet.user;

import io.github.oliviercailloux.diet.user.ReadEventJudgment.ReadEventJudgmentSerializer;
import java.time.Instant;
import javax.json.bind.annotation.JsonbTypeSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonbTypeSerializer(ReadEventJudgmentSerializer.class)
public class ReadEventJudgment extends ReadEvent {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadEventJudgment.class);

	public static class ReadEventJudgmentSerializer extends ReadEventSerializer<ReadEventJudgment> {
		@Override
		public void writeMore(ReadEventJudgment event, JsonGenerator generator, SerializationContext ctx) {
			ctx.serialize("judgment", event.judgment(), generator);
		}
	}

	public static ReadEventJudgment now(Judgment judgment) {
		return at(Instant.now(), judgment);
	}

	public static ReadEventJudgment at(Instant creation, Judgment judgment) {
		return new ReadEventJudgment(new EventJudgment(creation, judgment));
	}

	public static ReadEvent fromEvent(EventJudgment event) {
		return new ReadEventJudgment(event);
	}

	private ReadEventJudgment(EventJudgment event) {
		super(event);
	}

	public Judgment judgment() {
		return ((EventJudgment) underlyingEvent()).getJudgment();
	}

	@Override
	protected void persist(EntityManager em) {
		final Judgment judgment = judgment();
		if (!judgment.isPersistent()) {
			em.persist(judgment);
		}
		super.persist(em);
	}
}
