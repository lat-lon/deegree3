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
package org.deegree.filter.xml;

import org.apache.commons.io.IOUtils;
import org.deegree.commons.xml.stax.IndentingXMLStreamWriter;
import org.deegree.filter.Filter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xmlunit.matchers.CompareMatcher;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.IOUtils.toInputStream;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
// TODO: fix dependencies
@Disabled
public class Filter200XMLEncoderParameterizedTest {

	public static Stream<Arguments> data() throws IOException {
		return Stream.of(Arguments.of("testfilter1.xml", asString("v200/testfilter1.xml")),
				Arguments.of("testfilter3.xml", asString("v200/testfilter3.xml")),
				Arguments.of("testfilter4.xml", asString("v200/testfilter4.xml")),
				Arguments.of("testfilter5.xml", asString("v200/testfilter5.xml")),
				Arguments.of("testfilter6.xml", asString("v200/testfilter6.xml")),
				Arguments.of("testfilter7.xml", asString("v200/testfilter7.xml")),
				Arguments.of("testfilter8.xml", asString("v200/testfilter8.xml")),
				Arguments.of("aixm_by_gml_identifier.xml", asString("v200/aixm_by_gml_identifier.xml")),
				Arguments.of("aixm_custom_geometry_bbox.xml", asString("v200/aixm_custom_geometry_bbox.xml")),
				Arguments.of("aixm_custom_geometry_property.xml", asString("v200/aixm_custom_geometry_property.xml")),
				Arguments.of("aixm_timeinstant_begin.xml", asString("v200/aixm_timeinstant_begin.xml")),
				Arguments.of("temporal/tequals.xml", asString("v200/temporal/tequals.xml")),
				Arguments.of("bboxWithSpatialJoin.xml", asString("v200/bboxWithSpatialJoin.xml")),
				Arguments.of("beyondWithSpatialJoin.xml", asString("v200/beyondWithSpatialJoin.xml")),
				Arguments.of("containsWithSpatialJoin.xml", asString("v200/containsWithSpatialJoin.xml")),
				Arguments.of("crossesWithSpatialJoin.xml", asString("v200/crossesWithSpatialJoin.xml")),
				Arguments.of("disjointWithSpatialJoin.xml", asString("v200/disjointWithSpatialJoin.xml")),
				Arguments.of("dwithinWithSpatialJoin.xml", asString("v200/dwithinWithSpatialJoin.xml")),
				Arguments.of("equalsWithSpatialJoin.xml", asString("v200/equalsWithSpatialJoin.xml")),
				Arguments.of("intersectsWithSpatialJoin.xml", asString("v200/intersectsWithSpatialJoin.xml")),
				Arguments.of("overlapsWithSpatialJoin.xml", asString("v200/overlapsWithSpatialJoin.xml")),
				Arguments.of("touchesWithSpatialJoin.xml", asString("v200/touchesWithSpatialJoin.xml")),
				Arguments.of("withinWithSpatialJoin.xml", asString("v200/withinWithSpatialJoin.xml")));
	}

	@ParameterizedTest
	@MethodSource("data")
	public void testExport(String testName, String filterUnderTest) throws Exception {
		Filter filter = parseFilter(filterUnderTest);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(bos);
		IndentingXMLStreamWriter writer = new IndentingXMLStreamWriter(out);
		Filter200XMLEncoder.export(filter, writer);
		out.close();

		assertThat("Failed test: " + testName, bos.toString(),
				CompareMatcher.isSimilarTo(filterUnderTest).ignoreWhitespace());
	}

	private static String asString(String filterResource) throws IOException {
		InputStream resourceAsStream = Filter200XMLEncoderParameterizedTest.class.getResourceAsStream(filterResource);
		return IOUtils.toString(resourceAsStream, UTF_8);
	}

	private Filter parseFilter(String filterAsString) throws XMLStreamException, FactoryConfigurationError {
		XMLStreamReader in = XMLInputFactory.newInstance().createXMLStreamReader(toInputStream(filterAsString, UTF_8));
		in.nextTag();
		return Filter200XMLDecoder.parse(in);
	}

}