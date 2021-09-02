package io.github.oliviercailloux.diet.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.entity.Event;
import io.github.oliviercailloux.diet.entity.Video;
import java.util.Set;

@JsonPropertyOrder({ "username", "events", "seen", "toSee" })
public class StaticUserStatus {
	private final String username;
	private final ImmutableSet<Event> events;
	private final ImmutableSet<Video> seen;
	private final ImmutableSet<Video> toSee;

	public StaticUserStatus(String username, Set<Event> events, Set<Video> seen, Set<Video> toSee) {
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
