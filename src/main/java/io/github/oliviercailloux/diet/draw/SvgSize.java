package io.github.oliviercailloux.diet.draw;

record SvgSize(double x, double y) {

	public static SvgSize square(double length) {
		return new SvgSize(length, length);
	}

	public SvgSize plus(SvgSize p) {
		return new SvgSize(x + p.x, y + p.y);
	}

	public SvgSize mult(double factor) {
		return new SvgSize(x * factor, y * factor);
	}
}
