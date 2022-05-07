package io.github.oliviercailloux.diet.user;

import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.oliviercailloux.diet.draw.Drawer;
import io.github.oliviercailloux.diet.video.VideoFactory;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DrawerTests {

	@Inject
	VideoFactory factory;

	@Test
	void testDraw() throws Exception {
		final Drawer drawer = Drawer.drawer(factory.getAll());
		drawer.produce();
		assertFalse(true);
	}

}
