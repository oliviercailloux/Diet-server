package io.github.oliviercailloux.diet.draw;

import static com.google.common.base.Preconditions.checkArgument;

record GeneralSize(double x, double y) implements MathSize {

	public static GeneralSize square(double length) {
		return new GeneralSize(length, length);
	}

	public GeneralSize {
		checkArgument(Double.isFinite(x));
		checkArgument(Double.isFinite(y));
	}

	public GeneralSize plus(MathSize p) {
		return new GeneralSize(x + p.x(), y + p.y());
	}

	public GeneralSize mult(double factor) {
		return new GeneralSize(x * factor, y * factor);
	}
}
