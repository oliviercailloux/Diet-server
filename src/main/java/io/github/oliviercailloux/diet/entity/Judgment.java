package io.github.oliviercailloux.diet.entity;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class Judgment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonbTransient
	private int id;

	@NotNull
	private int daysVegan;
	@NotNull
	private int daysMeat;

	Judgment() {
		/* For JPA. */
	}

	@JsonbCreator
	public Judgment(@JsonbProperty("daysVegan") int daysVegan, @JsonbProperty("daysMeat") int daysMeat) {
		checkArgument(0 <= daysVegan);
		checkArgument(0 <= daysMeat);
		checkArgument(daysVegan + daysMeat <= 5);
		this.daysVegan = daysVegan;
		this.daysMeat = daysMeat;
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