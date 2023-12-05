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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.deegree.commons.utils.net.HttpUtils.STREAM;
import static org.deegree.commons.utils.net.HttpUtils.retrieve;
import static org.deegree.commons.utils.test.IntegrationTestUtils.isImageSimilar;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <code>TileLayerIT</code>
 *
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 */
public class TileLayerIntegrationTest {

	private final BufferedImage expected;

	private final String resourceName;

	private String request;

	public TileLayerIntegrationTest(String resourceName, String request) throws IOException {
		this.expected = ImageIO.read(TileLayerIntegrationTest.class.getResourceAsStream(resourceName));
		this.resourceName = resourceName;
		this.request = request;
	}

	private Stream<Arguments> getParameters() {
		return Stream.of(Arguments.of("maxextent.png",
				"?REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&WIDTH=978&HEIGHT=645&LAYERS=pyramid&TRANSPARENT=TRUE&FORMAT=image%2Fpng&BBOX=438742.26976744185,4448455.0,450241.7302325581,4456039.0&SRS=urn:opengis:def:crs:epsg::26912&STYLES="),
				Arguments.of("second.png",
						"?REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&WIDTH=978&HEIGHT=645&LAYERS=pyramid&TRANSPARENT=TRUE&FORMAT=image%2Fpng&BBOX=442054.1850365361,4450977.860706333,445834.7259965481,4453471.162259716&SRS=urn:opengis:def:crs:epsg::26912&STYLES="),
				Arguments.of("third.png",
						"?REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&WIDTH=978&HEIGHT=645&LAYERS=pyramid&TRANSPARENT=TRUE&FORMAT=image%2Fpng&BBOX=442923.9194703415,4451577.9980723625,444353.2355232765,4452520.645162488&SRS=urn:opengis:def:crs:epsg::26912&STYLES="));
	}

	@ParameterizedTest
	@MethodSource("getParameters")
	public void testSimilarity(String resourceName, String request) throws Exception {
		String base = createRequest();
		InputStream in = retrieve(STREAM, base);
		BufferedImage actual = ImageIO.read(in);
		assertTrue(isImageSimilar(expected, actual, 0.001, getClass().getName() + "_" + resourceName),
				"Image for " + resourceName + "are not similar enough");
	}

	private String createRequest() {
		StringBuffer sb = new StringBuffer();
		sb.append("http://localhost:");
		sb.append(System.getProperty("portnumber"));
		sb.append("/deegree-wms-tiling-tests/services");
		sb.append(request);
		return sb.toString();
	}

}
