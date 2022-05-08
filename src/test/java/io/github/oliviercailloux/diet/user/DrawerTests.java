package io.github.oliviercailloux.diet.user;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.oliviercailloux.diet.draw.Drawer;
import io.github.oliviercailloux.diet.video.VideoFactory;
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
	void testDraw() throws Exception {
		final Drawer drawer = Drawer.drawer(factory.getAll());
		final Document doc = drawer.html();

		final String html = DomHelper.domHelper().toString(doc);
		Files.writeString(Path.of("svg.xhtml"), html);
		LOGGER.info("Produced: {}.", html);
		assertTrue(html.contains("svg"));
	}

}
