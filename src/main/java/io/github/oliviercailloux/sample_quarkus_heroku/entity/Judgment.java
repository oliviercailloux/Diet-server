package io.github.oliviercailloux.sample_quarkus_heroku.entity;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Judgment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private int id;

	@NotNull
	private int daysVegan;
	@NotNull
	private int daysMeat;

	Judgment() {
		/* For JPA. */
	}

	public Judgment(int daysVegan, int daysMeat) {
		checkArgument(0 <= daysVegan);
		checkArgument(0 <= daysMeat);
		checkArgument(daysVegan + daysMeat <= 5);
	}

	public int getDaysVegan() {
		return daysVegan;
	}

	public int getDaysMeat() {
		return daysMeat;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("Days vegan", daysVegan).add("Days meat", daysMeat).toString();
	}

}