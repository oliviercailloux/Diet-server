package io.github.oliviercailloux.diet.video;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.oliviercailloux.diet.draw.Drawer;
import io.github.oliviercailloux.jaris.xml.DomHelper;
import io.quarkus.test.junit.QuarkusTest;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

@QuarkusTest
public class DrawerTests {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(DrawerTests.class);

	@Inject
	VideoFactory factory;

	@Test
	void html() throws Exception {
		final Drawer drawer = Drawer.drawer(factory.getAll());
		final Document doc = drawer.html();

		final String html = DomHelper.domHelper().toString(doc);
//		Files.writeString(Path.of("All videos.xhtml"), html);
//		LOGGER.info("Produced: {}.", html);
//		assertTrue(html.contains("svg"));
		final Path expectedPath = Path.of(getClass().getResource("All videos.xhtml").toURI());
		final String expected = Files.readString(expectedPath);
		assertEquals(expected, html);
	}

	@Test
	void svg() throws Exception {
		final Drawer drawer = Drawer.drawer(factory.getAll());
		final Document doc = drawer.svg();

		final String svg = DomHelper.domHelper().toString(doc);
		final Path expectedPath = Path.of(getClass().getResource("All videos.svg").toURI());
		final String expected = Files.readString(expectedPath);
		assertEquals(expected, svg);
	}

}
