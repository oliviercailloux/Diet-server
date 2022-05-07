package io.github.oliviercailloux.diet.draw;

record Point(int x, int y) {

	public Point plus(Point p) {
		return new Point(x + p.x, y + p.y);
	}
}
