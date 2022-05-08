package io.github.oliviercailloux.diet.draw;

import static com.google.common.base.Preconditions.checkArgument;

record SvgSize(double x, double y) implements MathSize {

	public static SvgSize square(double length) {
		return new SvgSize(length, length);
	}

	public SvgSize {
		checkArgument(Double.isFinite(x));
		checkArgument(Double.isFinite(y));
		checkArgument(x >= 0d);
		checkArgument(y >= 0d);
	}

	public SvgSize plus(MathSize p) {
		return new SvgSize(x + p.x(), y + p.y());
	}

	public MathSize multMath(double factor) {
		return new SvgSize(x * factor, y * factor);
	}

	public SvgSize mult(double factor) {
		checkArgument(factor >= 0d);
		return new SvgSize(x * factor, y * factor);
	}
}
