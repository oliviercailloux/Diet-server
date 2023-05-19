package io.github.oliviercailloux.diet.user;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.MoreObjects;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

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
		id = 0;
	}

	@JsonbCreator
	public Judgment(@JsonbProperty("daysVegan") int daysVegan, @JsonbProperty("daysMeat") int daysMeat) {
		checkArgument(0 <= daysVegan);
		checkArgument(0 <= daysMeat);
		checkArgument(daysVegan + daysMeat <= 5);
		this.daysVegan = daysVegan;
		this.daysMeat = daysMeat;
	}

	boolean isPersistent() {
		return id != 0;
	}

	public int getDaysVegan() {
		return daysVegan;
	}

	public int getDaysMeat() {
		return daysMeat;
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof Judgment)) {
			return false;
		}
		final Judgment t2 = (Judgment) o2;
		return daysVegan == t2.daysVegan && daysMeat == t2.daysMeat;
	}

	@Override
	public int hashCode() {
		return Objects.hash(daysVegan, daysMeat);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("Days vegan", daysVegan).add("Days meat", daysMeat).toString();
	}

}