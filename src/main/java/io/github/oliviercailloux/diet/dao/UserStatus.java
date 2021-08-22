package io.github.oliviercailloux.diet.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.entity.Event;
import io.github.oliviercailloux.diet.entity.User;
import io.github.oliviercailloux.diet.entity.Video;
import java.util.List;

@JsonPropertyOrder({ "username", "events", "seen", "toSee" })
public class UserStatus {
	private final User user;

	private final ImmutableList<Video> toSee;

	public UserStatus(User user, List<Video> toSee) {
		super();
		this.user = checkNotNull(user);
		this.toSee = ImmutableList.copyOf(toSee);
	}

	public String getUsername() {
		return user.getUsername();
	}

	public ImmutableSet<Event> getEvents() {
		return ImmutableSet.copyOf(user.getEvents());
	}

	public ImmutableSet<Video> getSeen() {
		return user.getSeen();
	}

	public ImmutableList<Video> getToSee() {
		return toSee;
	}
}
