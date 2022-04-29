package io.github.oliviercailloux.diet.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.entity.Event;
import io.github.oliviercailloux.diet.entity.Video;
import java.util.Set;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({ "username", "events", "seen", "toSee" })
public class StaticUserStatus {
	private final String username;
	private final ImmutableSet<Event> events;
	private final ImmutableSet<Video> seen;
	private final ImmutableSet<Video> toSee;

	@JsonbCreator
	public StaticUserStatus(@JsonbProperty("username") String username, @JsonbProperty("events") Set<Event> events,
			@JsonbProperty("seen") Set<Video> seen, @JsonbProperty("toSee") Set<Video> toSee) {
		this.username = checkNotNull(username);
		this.events = ImmutableSet.copyOf(events);
		this.seen = ImmutableSet.copyOf(seen);
		this.toSee = ImmutableSet.copyOf(toSee);
	}

	public String getUsername() {
		return username;
	}

	public ImmutableSet<Event> getEvents() {
		return events;
	}

	public ImmutableSet<Video> getSeen() {
		return seen;
	}

	public ImmutableSet<Video> getToSee() {
		return toSee;
	}
}
