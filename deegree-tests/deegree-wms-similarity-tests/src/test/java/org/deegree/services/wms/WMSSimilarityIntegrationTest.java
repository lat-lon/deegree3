/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 Occam Labs UG (haftungsbeschränkt)
 Godesberger Allee 139, 53175 Bonn
 Germany
 http://www.occamlabs.de/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/

package org.deegree.services.wms;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import static org.deegree.commons.utils.net.HttpUtils.IMAGE;
import static org.deegree.commons.utils.net.HttpUtils.retrieve;
import static org.deegree.commons.utils.test.IntegrationTestUtils.isImageSimilar;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <code>WMSSimilarityIntegrationTest</code>
 *
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 */

public class WMSSimilarityIntegrationTest {

	private Stream<Arguments> getParameters() {
		return Stream.of(Arguments.of("lines/lines_capbutt", "png", 0.01),
				Arguments.of("lines/lines_capround", "png", 0.01), Arguments.of("lines/lines_capsquare", "png", 0.01),
				Arguments.of("lines/lines_centroid", "png", 0.01), Arguments.of("lines/lines_dasharray", "png", 0.01),
				Arguments.of("lines/lines_dasharrayandoffset", "tif", 0.01),
				Arguments.of("lines/lines_divmod", "png", 0.01),
				Arguments.of("lines/lines_filtersamelayer", "png", 0.01),
				Arguments.of("lines/lines_getcurrentscale", "tif", 0.24),
				Arguments.of("lines/lines_graphicfill", "png", 0.01),
				Arguments.of("lines/lines_graphicstroke", "tif", 0.01),
				Arguments.of("lines/lines_joinbevel", "png", 0.01), Arguments.of("lines/lines_joinmitre", "png", 0.01),
				Arguments.of("lines/lines_joinround", "png", 0.01), Arguments.of("lines/lines_offset", "png", 0.01),
				Arguments.of("lines/lines_opacity", "png", 0.01), Arguments.of("lines/lines_pixelsize", "png", 0.01),
				Arguments.of("lines/lines_width5", "png", 0.01), Arguments.of("points/points_anchor0", "png", 0.01),
				Arguments.of("points/points_anchor1", "png", 0.01), Arguments.of("points/points_circle16", "png", 0.01),
				Arguments.of("points/points_circle32", "png", 0.01), Arguments.of("points/points_cross32", "png", 0.01),
				Arguments.of("points/points_defaultsquare", "png", 0.01),
				Arguments.of("points/points_displacementandanchorpoint", "png", 0.01),
				Arguments.of("points/points_displacementx", "png", 0.01),
				Arguments.of("points/points_displacementxnegative", "png", 0.01),
				Arguments.of("points/points_displacementy", "png", 0.01),
				Arguments.of("points/points_displacementynegative", "png", 0.01),
				Arguments.of("points/points_rotation", "png", 0.01), Arguments.of("points/points_star32", "png", 0.01),
				Arguments.of("points/points_triangle32", "png", 0.01), Arguments.of("points/points_x32", "png", 0.01),
				Arguments.of("polygons/polygons_edgedandsubstraction", "tif", 0.01),
				Arguments.of("polygons/polygons_offset", "tif", 0.01),
				Arguments.of("polygons/polygons_typepredicatetest", "tif", 0.01),
				Arguments.of("resolution/contours_parameter_dpi", "png", 0.01),
				Arguments.of("resolution/contours_parameter_format_options", "png", 0.01),
				Arguments.of("resolution/contours_parameter_map_resolution", "png", 0.01),
				Arguments.of("resolution/contours_parameter_pixelsize", "png", 0.01),
				Arguments.of("resolution/contours_parameter_res", "png", 0.01),
				Arguments.of("resolution/contours_parameter_x-dpi", "png", 0.01),
				Arguments.of("resolution/contours_vector_dpi_96", "png", 0.01),
				Arguments.of("resolution/contours_vector_dpi_100_empty", "png", 0.01),
				Arguments.of("resolution/contours_vector_dpi_192", "png", 0.01),
				Arguments.of("resolution/contours_vector_dpi_default_empty", "png", 0.01),
				Arguments.of("resolution/satellite_provo_dpi_96", "png", 0.01),
				Arguments.of("resolution/satellite_provo_dpi_100_empty", "png", 0.01),
				Arguments.of("resolution/satellite_provo_dpi_192", "png", 0.01),
				Arguments.of("resolution/satellite_provo_dpi_default_empty", "png", 0.01));
	}

	private String testName(String resourceName) {
		return getClass().getName() + "_" + resourceName.replaceAll("/", "_");
	}

	@ParameterizedTest
	@MethodSource("getParameters")
	public void testSimilarity(String resourceName, String format, double tolerance) throws Exception {
		String request = IOUtils
			.toString(WMSSimilarityIntegrationTest.class.getResourceAsStream("/requests/" + resourceName + ".kvp"));
		BufferedImage expected = ImageIO
			.read(WMSSimilarityIntegrationTest.class.getResourceAsStream("/requests/" + resourceName + "." + format));

		String base = createRequest(request);
		BufferedImage actual = retrieve(IMAGE, base);

		assertTrue(isImageSimilar(expected, actual, tolerance, testName(resourceName)),
				"Image for " + resourceName + "are not similar enough");
	}

	private String createRequest(String request) {
		StringBuffer sb = new StringBuffer();
		sb.append("http://localhost:");
		sb.append(System.getProperty("portnumber", "8080"));
		sb.append("/");
		sb.append(System.getProperty("deegree-wms-similarity-webapp", "deegree-wms-similarity-tests"));
		sb.append("/services");
		if (!request.startsWith("?"))
			sb.append("?");
		sb.append(request);
		return sb.toString();
	}

}
