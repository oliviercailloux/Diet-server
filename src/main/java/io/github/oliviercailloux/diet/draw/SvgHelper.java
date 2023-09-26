package io.github.oliviercailloux.diet.draw;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.jaris.xml.DomHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * From https://stackoverflow.com/q/26088839/
 * https://www.patreon.com/posts/svg-2-status-19078074 near death experience
 * http://tavmjong.free.fr/svg2_status.html SVG 2 is on life support
 * https://github.com/Fyrd/caniuse/issues/1143
 * 
 * So let’s go for SVG 1.1.
 */
public class SvgHelper {
	private static final String SVG = DomHelper.SVG_NS_URI.toString();

	public static SvgHelper using(Document document) {
		return new SvgHelper(document);
	}

	private final Document document;

	private SvgHelper(Document document) {
		this.document = checkNotNull(document);
	}

	public Element line(SvgPoint start, SvgPoint destination) {
		final Element line = document.createElementNS(SVG, "line");
		line.setAttribute("x1", String.valueOf(start.x()));
		line.setAttribute("y1", String.valueOf(start.y()));
		line.setAttribute("x2", String.valueOf(destination.x()));
		line.setAttribute("y2", String.valueOf(destination.y()));
		return line;
	}

	public Element ellipse(SvgPoint position, SvgSize semiSize) {
		final Element ell = document.createElementNS(SVG, "ellipse");
		if (!position.equals(SvgPoint.zero())) {
			ell.setAttribute("cx", String.valueOf(position.x()));
			ell.setAttribute("cy", String.valueOf(position.y()));
		}
		ell.setAttribute("rx", String.valueOf(semiSize.x()));
		ell.setAttribute("ry", String.valueOf(semiSize.y()));
		return ell;
	}

	public Element foreignCenteredAt(SvgPoint center, SvgSize size) {
		final Element foreignForDescription = document.createElementNS(SVG, "foreignObject");
		foreignForDescription.setAttribute("x", String.valueOf(center.x() - size.x() / 2d));
		foreignForDescription.setAttribute("y", String.valueOf(center.y() - size.y() / 2d));
		foreignForDescription.setAttribute("width", String.valueOf(size.x()));
		foreignForDescription.setAttribute("height", String.valueOf(size.y()));
		return foreignForDescription;
	}

	public Element useCenteredAt(SvgPoint center, SvgSize size) {
		final SvgPoint corner = new SvgPoint(center.x() - size.x() / 2d, center.y() - size.y() / 2d);
		return useCorneredAt(corner, size);
	}

	public Element useCorneredAt(SvgPoint corner, SvgSize size) {
		final Element foreignForDescription = document.createElementNS(SVG, "use");
		foreignForDescription.setAttribute("x", String.valueOf(corner.x()));
		foreignForDescription.setAttribute("y", String.valueOf(corner.y()));
		foreignForDescription.setAttribute("width", String.valueOf(size.x()));
		foreignForDescription.setAttribute("height", String.valueOf(size.y()));
		return foreignForDescription;
	}

	public Element setSize(Element svgElement, SvgSize size) {
		svgElement.setAttribute("width", String.valueOf(size.x()));
		svgElement.setAttribute("height", String.valueOf(size.y()));
		return svgElement;
	}

}
