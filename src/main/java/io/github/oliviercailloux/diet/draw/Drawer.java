package io.github.oliviercailloux.diet.draw;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
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

	private static final Ellipse ELLIPSE = Ellipse.optimal(60d);

	private static final SvgSize SPACE = ELLIPSE.semiSize().mult(1.75d);

	public static Drawer drawer(Set<? extends VideoWithCounters> videos) {
		return new Drawer(videos);
	}

	private final ImmutableSet<VideoWithCounters> videos;
	private ImmutableBiMap<Integer, VideoWithCounters> byId;
	private ImmutableBiMap<Video, Point> abstractPositions;
	private ImmutableBiMap<Video, SvgPoint> svgPositions;

	private Document document;

	private SvgCreator creator;

	private Drawer(Set<? extends VideoWithCounters> videos) {
		this.videos = ImmutableSet.copyOf(videos);
		byId = null;
		abstractPositions = null;
		svgPositions = null;
		document = null;
		creator = null;
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

	private SvgPoint toSvg(Point abstractPoint) {
		final double spaceX = abstractPoint.x() * SPACE.x();
		final double spaceY = abstractPoint.y() * SPACE.y();
		final SvgPoint space = new SvgPoint(spaceX, spaceY);
		return space.plus(ELLIPSE.semiSize());
	}

	private void computeSvgPositions() {
		svgPositions = videos.stream()
				.collect(ImmutableBiMap.toImmutableBiMap(v -> v, v -> toSvg(abstractPositions.get(v))));
	}

	private Element line(SvgPoint start, SvgPoint destination) {
		final Element line = document.createElementNS(SVG, "line");
		line.setAttribute("x1", String.valueOf(start.x()));
		line.setAttribute("y1", String.valueOf(start.y()));
		line.setAttribute("x2", String.valueOf(destination.x()));
		line.setAttribute("y2", String.valueOf(destination.y()));
		line.setAttribute("stroke", "black");
		return line;
	}

	private ImmutableSet<Element> linesFrom(VideoWithCounters start) {
		final SvgPoint startPoint = svgPositions.get(start);
		final ImmutableSortedSet<Video> dests = start.counteredBy();
		final ImmutableSet<SvgPoint> destPoints = dests.stream().map(svgPositions::get)
				.collect(ImmutableSet.toImmutableSet());
		final ImmutableSet<Element> lines = destPoints.stream().map(d -> line(startPoint, d))
				.collect(ImmutableSet.toImmutableSet());
		return lines;
	}

	private Element ellipseGroup(VideoWithCounters v) {
		final Element globalGroup = document.createElementNS(SVG, "g");
		{
			final SvgPoint point = svgPositions.get(v);
			globalGroup.setAttribute("transform", "translate" + point.coords());
		}

		final Element ell = creator.ellipse(SvgPoint.zero(), ELLIPSE.semiSize());
		{
			final String sideClass = "side-" + v.getSide().toString();
			final String reachability = v.counters().isEmpty() ? "reachable" : "unreachable";
			ell.setAttribute("class", sideClass + " " + reachability);
		}
		globalGroup.appendChild(ell);

		final Element foreignForDescription = creator.foreignCenteredAt(SvgPoint.zero(),
				ELLIPSE.inscribedRectangleSize());
		{
			foreignForDescription.setAttribute("class", "video-description-parent");
		}
		globalGroup.appendChild(foreignForDescription);

		final Element pForDescription = document.createElementNS(HTML, "p");
		{
			pForDescription.setAttribute("class", "video-description");
			pForDescription.appendChild(document.createTextNode(v.getDescription()));
		}
		foreignForDescription.appendChild(pForDescription);

		final SvgSize coveringSize;
		{
			final double widthPerHeight = 16d / 9d;
			/*
			 * We want at least height ellipse and at least width of ellipse so that the
			 * whole ellipse is covered.
			 */
			final double widthIfFitToWidth = ELLIPSE.size().x();
			final double widthIfFitToHeight = widthPerHeight * ELLIPSE.size().y();
			final double effectiveWidth = Math.max(widthIfFitToWidth, widthIfFitToHeight);
			final double effectiveHeight = effectiveWidth / widthPerHeight;
			coveringSize = new SvgSize(effectiveWidth, effectiveHeight);
		}
		final Element foreignForVideo = creator.foreignCenteredAt(SvgPoint.zero(), coveringSize);
		{
			foreignForVideo.setAttribute("class", "foreign-video");
			final Element vE = document.createElementNS(HTML, "video");
			vE.setAttribute("width", "100%");
			vE.setAttribute("height", "100%");
			vE.setAttribute("hidden", "hidden");
			foreignForVideo.appendChild(vE);
			final Element videoSource = document.createElementNS(HTML, "source");
			videoSource.setAttribute("src", v.getUrl().toString());
			vE.appendChild(videoSource);
		}
		globalGroup.appendChild(foreignForVideo);

		final double inscribedSemiHeight = ELLIPSE.inscribedRectangleSize().y() / 2;
		final double remainingHeight = ELLIPSE.semiSize().y() - inscribedSemiHeight;
		final Element use = creator.useCorneredAt(new SvgPoint(-remainingHeight / 2d, inscribedSemiHeight),
				SvgSize.square(remainingHeight));
//		document.createEntityReference("play");
		use.setAttribute("href", "#play");
		globalGroup.appendChild(use);

		return globalGroup;
	}

	private void populateSvg(Element svgRoot) {
		svgRoot.setAttribute("width", "4000");
		svgRoot.setAttribute("height", "4500");

		final Element clipPath = document.createElementNS(SVG, "clipPath");
		clipPath.setAttribute("id", "video-clip");
		final Element clipEllipse = creator.ellipse(SvgPoint.zero(),
				ELLIPSE.semiSize().plus(new GeneralSize(-1d, -1d)));
		clipPath.appendChild(clipEllipse);
		svgRoot.appendChild(clipPath);

		final ImmutableSet<Element> lines = videos.stream().flatMap(v -> linesFrom(v).stream())
				.collect(ImmutableSet.toImmutableSet());
		lines.stream().forEach(svgRoot::appendChild);

		final ImmutableSet<Element> ellipses = videos.stream().map(this::ellipseGroup)
				.collect(ImmutableSet.toImmutableSet());
		ellipses.stream().forEach(svgRoot::appendChild);
	}

	public Document svg() {
		byId = videos.stream().collect(ImmutableBiMap.toImmutableBiMap(VideoWithCounters::getFileId, v -> v));
		initAbstractPositions();
		computeSvgPositions();

		document = DomHelper.domHelper().svg();
		creator = SvgCreator.using(document);
		final Element svgRoot = document.getDocumentElement();
		populateSvg(svgRoot);
		return document;
	}

	public Document html() {
		byId = videos.stream().collect(ImmutableBiMap.toImmutableBiMap(VideoWithCounters::getFileId, v -> v));
		initAbstractPositions();
		computeSvgPositions();

		document = DomHelper.domHelper().html();
		creator = SvgCreator.using(document);
		final Element html = document.getDocumentElement();

		final Element head = document.createElementNS(HTML, "head");
		html.appendChild(head);

		final Element scriptFA = document.createElementNS(HTML, "script");
		scriptFA.setAttribute("src", "https://use.fontawesome.com/releases/v5.15.3/js/all.js");
		head.appendChild(scriptFA);

		final Element link = document.createElementNS(HTML, "link");
		link.setAttribute("rel", "stylesheet");
		link.setAttribute("href", "svg.css");
		head.appendChild(link);

		final Element body = document.createElementNS(HTML, "body");
		html.appendChild(body);

		final Element spanPlay = document.createElementNS(HTML, "span");
		spanPlay.setAttribute("data-fa-symbol", "play");
		spanPlay.setAttribute("class", "fas fa-play");
		body.appendChild(spanPlay);

		final Element svgRoot = document.createElementNS(SVG, "svg");
		body.appendChild(svgRoot);

		populateSvg(svgRoot);
		return document;
	}
}
