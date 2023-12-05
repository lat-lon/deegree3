/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2015 by:
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

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.protocol.wfs.getfeature.kvp;

import org.deegree.commons.utils.kvp.KVPUtils;
import org.deegree.protocol.wfs.getfeature.GetFeature;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
// TODO: fix dependencies
@Disabled
public class GetFeature200KVPEncoderParameterizedTest {

	private static Stream<Arguments> data() throws IOException {
		return Stream.of(Arguments.of("example1.kvp", asKvp("wfs200/example1.kvp")),
				Arguments.of("example2.kvp", asKvp("wfs200/example2.kvp")),
				Arguments.of("example3.kvp", asKvp("wfs200/example2.kvp")),
				Arguments.of("example4.kvp", asKvp("wfs200/example4.kvp")),
				Arguments.of("example5.kvp", asKvp("wfs200/example5.kvp")),
				Arguments.of("example7.kvp", asKvp("wfs200/example7.kvp")),
				Arguments.of("example11.kvp", asKvp("wfs200/example11.kvp")),
				Arguments.of("example12.kvp", asKvp("wfs200/example12.kvp")),
				Arguments.of("example13.kvp", asKvp("wfs200/example13.kvp")),
				Arguments.of("example16.kvp", asKvp("wfs200/example16.kvp")),
				Arguments.of("example17.kvp", asKvp("wfs200/example17.kvp")),
				Arguments.of("example18.kvp", asKvp("wfs200/example18.kvp")),
				Arguments.of("example19.kvp", asKvp("wfs200/example19.kvp")),
				Arguments.of("example20.kvp", asKvp("wfs200/example20.kvp")),
				Arguments.of("example21.kvp", asKvp("wfs200/example21.kvp")),
				Arguments.of("example_bbox_explicit_crs.kvp", asKvp("wfs200/example_bbox_explicit_crs.kvp")));
	}

	@ParameterizedTest
	@MethodSource("date")
	public void testExport(String testName, Map<String, String> kvpMapUnderTest) throws Exception {
		GetFeature getFeature = GetFeatureKVPAdapter.parse(kvpMapUnderTest, null);
		Map<String, String> exportedKvp = GetFeature200KVPEncoder.export(getFeature);

		assertEquals(exportedKvp, kvpMapUnderTest, "Failed test resource: " + testName);
	}

	private static Map<String, String> asKvp(String name) throws IOException {
		URL exampleURL = GetFeature200KVPEncoderParameterizedTest.class.getResource(name);
		return KVPUtils.readFileIntoMap(exampleURL);
	}

}