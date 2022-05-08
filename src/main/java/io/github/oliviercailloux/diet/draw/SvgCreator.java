package io.github.oliviercailloux.diet.draw;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.oliviercailloux.jaris.xml.DomHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SvgCreator {
	private static final String SVG = DomHelper.SVG_NS_URI.toString();

	public static SvgCreator using(Document document) {
		return new SvgCreator(document);
	}

	private final Document document;

	private SvgCreator(Document document) {
		this.document = checkNotNull(document);
	}

	public Element ellipse(SvgPoint position, SvgSize size) {
		final Element ell = document.createElementNS(SVG, "ellipse");
		if (!position.equals(SvgPoint.zero())) {
			ell.setAttribute("cx", String.valueOf(position.x()));
			ell.setAttribute("cy", String.valueOf(position.y()));
		}
		ell.setAttribute("rx", String.valueOf(size.x()));
		ell.setAttribute("ry", String.valueOf(size.y()));
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

}
