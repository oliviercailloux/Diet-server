package io.github.oliviercailloux.diet.draw;

record SvgPoint(double x, double y) {

	static SvgPoint zero() {
		return new SvgPoint(0d, 0d);
	}

	public SvgPoint plus(SvgPoint p) {
		return new SvgPoint(x + p.x, y + p.y);
	}

	public SvgPoint plus(SvgSize p) {
		return new SvgPoint(x + p.x(), y + p.y());
	}

	public SvgPoint max(SvgPoint p) {
		return new SvgPoint(Math.max(x, p.x()), Math.max(y, p.y()));
	}

	public String coords() {
		return "(" + String.valueOf(x) + ", " + String.valueOf(y) + ")";
	}
}
