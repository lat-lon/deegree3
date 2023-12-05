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

package org.deegree.services.wmts;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.deegree.commons.utils.net.HttpUtils.STREAM;
import static org.deegree.commons.utils.net.HttpUtils.retrieve;
import static org.deegree.commons.utils.test.IntegrationTestUtils.isImageSimilar;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * <code>WMTSIntegrationTest</code>
 *
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 */

public class WmtsGetTileSimilarityIT extends AbstractWmtsSimilarityIT {

	private static final Logger LOG = getLogger(WmtsGetTileSimilarityIT.class);

	private Stream<Arguments> getParameters() {
		return Stream.of(Arguments.of("cache1"), Arguments.of("cache2"), Arguments.of("filesystem1"),
				Arguments.of("filesystem2"), Arguments.of("remotewms1"), Arguments.of("remotewms130"),
				Arguments.of("remotewms_cache"), Arguments.of("remotewms_cache2"),
				// requests.add( new Object[] { "remotewms_gif" } );
				Arguments.of("remotewmts"), Arguments.of("transparent"), Arguments.of("uncached1"),
				Arguments.of("uncached2"), Arguments.of("utah4326"), Arguments.of("utah4326_130"));
	}

	@ParameterizedTest
	@MethodSource("getParameters")
	public void testSimilarity(String resourceName) throws Exception {
		String kvpRequest = IOUtils
			.toString(WmtsGetFeatureInfoSimilarityIT.class.getResourceAsStream("/getTile/" + resourceName + ".kvp"));
		BufferedImage expected = ImageIO
			.read(WmtsGetFeatureInfoSimilarityIT.class.getResourceAsStream("/getTile/" + resourceName + ".response"));

		String request = createRequest(kvpRequest);
		InputStream in = retrieve(STREAM, request);
		LOG.info("Requesting {}", request);
		BufferedImage actual = ImageIO.read(in);

		assertTrue(isImageSimilar(expected, actual, 0.01, getClass().getName() + "_" + resourceName),
				"Image for " + resourceName + "are not similar enough");
	}

}
