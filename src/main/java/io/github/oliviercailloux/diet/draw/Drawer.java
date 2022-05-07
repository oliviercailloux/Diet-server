package io.github.oliviercailloux.diet.draw;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import io.github.oliviercailloux.diet.video.Video;
import io.github.oliviercailloux.diet.video.VideoWithCounters;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Drawer {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(Drawer.class);

	private static final String HTML = DomHelper.HTML_NS_URI.toString();

	private static final String SVG = DomHelper.SVG_NS_URI.toString();

	private static final Point ELLIPSE_SEMI = new Point(80, 40);
	private static final Point SPACE = ELLIPSE_SEMI.plus(ELLIPSE_SEMI).plus(new Point(20, 10));

	public static Drawer drawer(Set<? extends VideoWithCounters> videos) {
		return new Drawer(videos);
	}

	private final ImmutableSet<VideoWithCounters> videos;
	private ImmutableBiMap<Integer, VideoWithCounters> byId;
	private ImmutableBiMap<Video, Point> abstractPositions;
	private ImmutableBiMap<Video, Point> svgPositions;

	private Drawer(Set<? extends VideoWithCounters> videos) {
		this.videos = ImmutableSet.copyOf(videos);
		byId = null;
		abstractPositions = null;
		svgPositions = null;
	}

	private void initAbstractPositions() {
		final ImmutableBiMap.Builder<Video, Point> builder = ImmutableBiMap.builder();
		builder.put(byId.get(16), new Point(0, 0));
		builder.put(byId.get(2), new Point(2, 0));
		builder.put(byId.get(6), new Point(4, 0));
		builder.put(byId.get(5), new Point(6, 0));
		builder.put(byId.get(4), new Point(8, 0));
		builder.put(byId.get(1), new Point(10, 0));
		builder.put(byId.get(3), new Point(1, 1));
		builder.put(byId.get(7), new Point(3, 1));
		builder.put(byId.get(8), new Point(5, 1));
		builder.put(byId.get(9), new Point(7, 1));
		builder.put(byId.get(10), new Point(9, 1));
		builder.put(byId.get(11), new Point(2, 2));
		builder.put(byId.get(12), new Point(4, 2));
		builder.put(byId.get(13), new Point(6, 2));
		builder.put(byId.get(14), new Point(8, 2));
		builder.put(byId.get(15), new Point(10, 2));
		abstractPositions = builder.build();
		checkState(abstractPositions.keySet().equals(videos));
	}

	private Point toSvg(Point abstractPoint) {
		final int spaceX = abstractPoint.x() * SPACE.x();
		final int spaceY = abstractPoint.y() * SPACE.y();
		final Point space = new Point(spaceX, spaceY);
		return ELLIPSE_SEMI.plus(space);
	}

	private void computeSvgPositions() {
		svgPositions = videos.stream()
				.collect(ImmutableBiMap.toImmutableBiMap(v -> v, v -> toSvg(abstractPositions.get(v))));
	}

	private void populateSvg(Element svgRoot) {
		final Document doc = svgRoot.getOwnerDocument();

		svgRoot.setAttribute("width", "400");
		svgRoot.setAttribute("height", "450");

		Element rectangle = doc.createElementNS(SVG, "rect");
		rectangle.setAttribute("x", "10");
		rectangle.setAttribute("y", "20");
		rectangle.setAttribute("width", "100");
		rectangle.setAttribute("height", "50");
		rectangle.setAttribute("fill", "red");

		svgRoot.appendChild(rectangle);
	}

	public Document html() {
		byId = videos.stream().collect(ImmutableBiMap.toImmutableBiMap(VideoWithCounters::getFileId, v -> v));
		initAbstractPositions();
		computeSvgPositions();

		final Document doc = DomHelper.domHelper().html();
		final Element html = doc.getDocumentElement();

		final Element body = doc.createElementNS(HTML, "body");
		html.appendChild(body);

		final Element svgRoot = doc.createElementNS(SVG, "svg");
		body.appendChild(svgRoot);

		populateSvg(svgRoot);
		return doc;
	}

	public Document svg() {
		byId = videos.stream().collect(ImmutableBiMap.toImmutableBiMap(VideoWithCounters::getFileId, v -> v));
		initAbstractPositions();
		computeSvgPositions();

		final Document doc = DomHelper.domHelper().svg();
		final Element svgRoot = doc.getDocumentElement();
		populateSvg(svgRoot);
		return doc;
	}
}
