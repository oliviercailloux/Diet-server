package io.github.oliviercailloux.diet.draw;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Ellipse {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Ellipse.class);

	static Ellipse optimal(double semiHeight) {
		return ab(new SvgSize(semiHeight * Math.sqrt(2d), semiHeight));
	}

	static Ellipse ab(SvgSize semiSize) {
		return new Ellipse(semiSize);
	}

	static Ellipse fullSize(SvgSize size) {
		return ab(size.mult(1d / 2d));
	}

	private final SvgSize semiSize;

	private Ellipse(SvgSize size) {
		this.semiSize = checkNotNull(size);
	}

	public SvgSize semiSize() {
		return semiSize;
	}

	public SvgSize size() {
		return semiSize.mult(2d);
	}

	public SvgSize inscribedSquareSize() {
		return size().mult(Math.sqrt(2d));
	}

	@Override
	public boolean equals(Object o2) {
		if (!(o2 instanceof Ellipse)) {
			return false;
		}
		final Ellipse t2 = (Ellipse) o2;
		return semiSize.equals(t2.semiSize);
	}

	@Override
	public int hashCode() {
		return Objects.hash(semiSize);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("Semi size", semiSize).toString();
	}
}
