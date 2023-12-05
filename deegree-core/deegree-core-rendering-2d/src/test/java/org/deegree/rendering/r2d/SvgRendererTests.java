package org.deegree.rendering.r2d;

import org.deegree.style.styling.components.Graphic;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// TODO: fix dependencies
@Disabled
public class SvgRendererTests {

	private static final Logger LOG = LoggerFactory.getLogger(SvgRendererTests.class);

	@ParameterizedTest
	@MethodSource("data")
	public void testGeneratedImage(int requiredWidth, int requiredHeight, int requestedWidth, int requestedHeight,
			String fileName) {
		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, requestedWidth, requestedHeight);
		Graphic g = new Graphic();
		g.size = requestedHeight > 0 ? requestedHeight : -requestedWidth;
		g.imageURL = getClass().getResource("svgtests/" + fileName).toExternalForm();

		BufferedImage img = (new SvgRenderer()).prepareSvg(rect, g);

		assertNotNull(img);
		LOG.info("generated image w: {} h: {} from: {}", img.getWidth(), img.getHeight(), fileName);
		assertEquals(requiredWidth, img.getWidth());
		assertEquals(requiredHeight, img.getHeight());
	}

	private Stream<Arguments> data() {
		// target width, height, rectWidth, rectHeight, file
		return Stream.of(Arguments.of(183, 100, 0, 100, "svg_w200_h100_border10.svg"),
				Arguments.of(100, 55, 100, 0, "svg_w200_h100_border10.svg"),
				Arguments.of(100, 100, 100, 100, "svg_w200_h100_border10.svg"),
				Arguments.of(220, 120, 0, 0, "svg_w200_h100_border10.svg"),
				Arguments.of(200, 100, 0, 100, "svg_w200_h100_no_border.svg"),
				Arguments.of(100, 50, 100, 0, "svg_w200_h100_no_border.svg"),
				Arguments.of(100, 100, 100, 100, "svg_w200_h100_no_border.svg"),
				Arguments.of(200, 100, 0, 0, "svg_w200_h100_no_border.svg"),
				Arguments.of(100, 183, 100, 0, "svg_w100_h200_border10.svg"),
				Arguments.of(55, 100, 0, 100, "svg_w100_h200_border10.svg"),
				Arguments.of(100, 100, 100, 100, "svg_w100_h200_border10.svg"),
				Arguments.of(120, 220, 0, 0, "svg_w100_h200_border10.svg"),
				Arguments.of(100, 200, 100, 0, "svg_w100_h200_no_border.svg"),
				Arguments.of(50, 100, 0, 100, "svg_w100_h200_no_border.svg"),
				Arguments.of(100, 100, 100, 100, "svg_w100_h200_no_border.svg"),
				Arguments.of(100, 200, 0, 0, "svg_w100_h200_no_border.svg"));
	}

}
