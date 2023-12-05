/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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

import org.deegree.commons.tom.primitive.PrimitiveValue;
import org.deegree.commons.xml.XMLParsingException;
import org.deegree.commons.xml.schema.RedirectingEntityResolver;
import org.deegree.commons.xml.stax.XMLStreamUtils;
import org.deegree.filter.Filter;
import org.deegree.filter.IdFilter;
import org.deegree.filter.Operator;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.comparison.PropertyIsBetween;
import org.deegree.filter.comparison.PropertyIsEqualTo;
import org.deegree.filter.comparison.PropertyIsLessThan;
import org.deegree.filter.comparison.PropertyIsLike;
import org.deegree.filter.expression.Function;
import org.deegree.filter.expression.Literal;
import org.deegree.filter.expression.ValueReference;
import org.deegree.filter.logical.And;
import org.deegree.filter.logical.Not;
import org.deegree.filter.spatial.BBOX;
import org.deegree.filter.spatial.Beyond;
import org.deegree.filter.spatial.Contains;
import org.deegree.filter.spatial.Crosses;
import org.deegree.filter.spatial.DWithin;
import org.deegree.filter.spatial.Disjoint;
import org.deegree.filter.spatial.Equals;
import org.deegree.filter.spatial.Intersects;
import org.deegree.filter.spatial.Overlaps;
import org.deegree.filter.spatial.Touches;
import org.deegree.filter.spatial.Within;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.primitive.Polygon;
import org.deegree.workspace.standard.DefaultWorkspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the correct parsing of Filter Encoding 2.0.0 documents using the official
 * examples (excluding filter capabilities examples and filter examples with custom
 * operations).
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 */
public class Filter200XMLDecoderTest {

	// files are not really fetched from this URL, but taken from cached version (module
	// deegree-ogcschemas)
	private static final String OGC_EXAMPLES_BASE_URL = "http://schemas.opengis.net/filter/2.0/examples/";

	@BeforeEach
	public void setUp() throws Exception {
		new DefaultWorkspace(new File("nix")).initAll();
	}

	@Test
	public void parseOGCExample01() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter01.xml")) {
			PropertyIsEqualTo op = (PropertyIsEqualTo) ((OperatorFilter) filter).getOperator();
			ValueReference valRef = (ValueReference) op.getParameter1();
			assertEquals("SomeProperty", valRef.getAsText());
			assertEquals(new QName("SomeProperty"), valRef.getAsQName());
			@SuppressWarnings("unchecked")
			Literal<PrimitiveValue> literal = (Literal<PrimitiveValue>) op.getParameter2();
			PrimitiveValue pv = literal.getValue();
			assertEquals("100", pv.getValue());
		}
	}

	@Test
	public void parseOGCExample02() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter02.xml")) {
			PropertyIsLessThan op = (PropertyIsLessThan) ((OperatorFilter) filter).getOperator();
			ValueReference valRef = (ValueReference) op.getParameter1();
			assertEquals("DEPTH", valRef.getAsText());
			assertEquals(new QName("DEPTH"), valRef.getAsQName());
			@SuppressWarnings("unchecked")
			Literal<PrimitiveValue> literal = (Literal<PrimitiveValue>) op.getParameter2();
			PrimitiveValue pv = literal.getValue();
			assertEquals("30", pv.getValue());
		}
	}

	@Test
	public void parseOGCExample03() throws XMLStreamException, FactoryConfigurationError, IOException {

		URL[] urls = getUrls("filter03.xml");
		for (int i = 1; i < urls.length; i++) {
			// gml 2.1.2 version of this example is borked (coordinates)
			InputStream is = urls[i].openStream();
			XMLStreamReader xmlStream = XMLInputFactory.newInstance().createXMLStreamReader(is);
			XMLStreamUtils.skipStartDocument(xmlStream);
			Filter filter = Filter200XMLDecoder.parse(xmlStream);

			Not not = (Not) ((OperatorFilter) filter).getOperator();
			Disjoint op = (Disjoint) not.getParameter();
			ValueReference valRef = (ValueReference) op.getParam1();
			assertEquals("Geometry", valRef.getAsText());
			assertEquals(new QName("Geometry"), valRef.getAsQName());
			Envelope env = (Envelope) op.getGeometry();
			// assertEquals( "urn:fes:def:crs:EPSG::4326",
			// env.getCoordinateSystem().getName() );
		}
	}

	@Test
	public void parseOGCExample04() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter04.xml")) {
			And and = (And) ((OperatorFilter) filter).getOperator();

			PropertyIsLessThan propIsLessThan = (PropertyIsLessThan) and.getParams()[0];
			ValueReference valRef = (ValueReference) propIsLessThan.getParameter1();
			assertEquals("DEPTH", valRef.getAsText());
			assertEquals(new QName("DEPTH"), valRef.getAsQName());
			@SuppressWarnings("unchecked")
			Literal<PrimitiveValue> literal = (Literal<PrimitiveValue>) propIsLessThan.getParameter2();
			PrimitiveValue pv = literal.getValue();
			assertEquals("30", pv.getValue());

			Not not = (Not) and.getParams()[1];
			Disjoint op = (Disjoint) not.getParameter();
			valRef = (ValueReference) op.getParam1();
			assertEquals("Geometry", valRef.getAsText());
			assertEquals(new QName("Geometry"), valRef.getAsQName());
			Envelope env = (Envelope) op.getGeometry();
			// assertEquals( "urn:fes:def:crs:EPSG::4326",
			// env.getCoordinateSystem().getName() );
		}
	}

	@Test
	public void parseOGCExample05() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter05.xml")) {
			IdFilter idFilter = (IdFilter) filter;
			assertTrue(idFilter.getSelectedIds().size() == 6);
			assertEquals("TREESA_1M.1234", idFilter.getSelectedIds().get(0).getRid());
			assertEquals("TREESA_1M.5678", idFilter.getSelectedIds().get(1).getRid());
			assertEquals("TREESA_1M.9012", idFilter.getSelectedIds().get(2).getRid());
			assertEquals("INWATERA_1M.3456", idFilter.getSelectedIds().get(3).getRid());
			assertEquals("INWATERA_1M.7890", idFilter.getSelectedIds().get(4).getRid());
			assertEquals("BUILTUPA_1M.4321", idFilter.getSelectedIds().get(5).getRid());
		}
	}

	@Test
	public void parseOGCExample06() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter06.xml")) {
			PropertyIsEqualTo op = (PropertyIsEqualTo) ((OperatorFilter) filter).getOperator();
			Function function = (Function) op.getParameter1();
			assertEquals("SIN", function.getName());
			ValueReference valRef = (ValueReference) function.getParams()[0];
			assertEquals("DISPERSION_ANGLE", valRef.getAsText());
			@SuppressWarnings("unchecked")
			Literal<PrimitiveValue> literal = (Literal<PrimitiveValue>) op.getParameter2();
			PrimitiveValue pv = literal.getValue();
			assertEquals("1", pv.getValue());
		}
	}

	@Test
	public void parseOGCExample07() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter07.xml")) {
			PropertyIsEqualTo op = (PropertyIsEqualTo) ((OperatorFilter) filter).getOperator();

			ValueReference valRef = (ValueReference) op.getParameter1();
			assertEquals("PROPA", valRef.getAsText());

			Function function = (Function) op.getParameter2();
			assertEquals("Add", function.getName());
			valRef = (ValueReference) function.getParams()[0];
			assertEquals("PROPB", valRef.getAsText());
			@SuppressWarnings("unchecked")
			Literal<PrimitiveValue> literal = (Literal<PrimitiveValue>) function.getParams()[1];
			PrimitiveValue pv = literal.getValue();
			assertEquals("100", pv.getValue());
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void parseOGCExample08() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter08.xml")) {
			PropertyIsBetween op = (PropertyIsBetween) ((OperatorFilter) filter).getOperator();

			ValueReference valRef = (ValueReference) op.getExpression();
			assertEquals("DEPTH", valRef.getAsText());

			Literal<PrimitiveValue> literal = (Literal<PrimitiveValue>) op.getLowerBoundary();
			PrimitiveValue pv = literal.getValue();
			assertEquals("100", pv.getValue());

			literal = (Literal<PrimitiveValue>) op.getUpperBoundary();
			pv = literal.getValue();
			assertEquals("200", pv.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void parseOGCExample09() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter09.xml")) {
			PropertyIsBetween op = (PropertyIsBetween) ((OperatorFilter) filter).getOperator();

			ValueReference valRef = (ValueReference) op.getExpression();
			assertEquals("SAMPLE_DATE", valRef.getAsText());

			Literal<PrimitiveValue> literal = (Literal<PrimitiveValue>) op.getLowerBoundary();
			PrimitiveValue pv = literal.getValue();
			assertEquals("2001-01-15T20:07:48.11", pv.getValue());

			literal = (Literal<PrimitiveValue>) op.getUpperBoundary();
			pv = literal.getValue();
			assertEquals("2001-03-06T12:00:00.00", pv.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void parseOGCExample10() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter10.xml")) {
			PropertyIsLike op = (PropertyIsLike) ((OperatorFilter) filter).getOperator();
			assertEquals("*", op.getWildCard());
			assertEquals("#", op.getSingleChar());
			assertEquals("!", op.getEscapeChar());
			ValueReference valRef = (ValueReference) op.getExpression();
			assertEquals("LAST_NAME", valRef.getAsText());
			Literal<PrimitiveValue> literal = (Literal<PrimitiveValue>) op.getPattern();
			PrimitiveValue pv = literal.getValue();
			assertEquals("JOHN*", pv.getValue());
		}
	}

	@Test
	public void parseOGCExample11() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter11.xml")) {
			Overlaps op = (Overlaps) ((OperatorFilter) filter).getOperator();
			ValueReference valRef = (ValueReference) op.getParam1();
			assertEquals("Geometry", valRef.getAsText());
			Polygon polygon = (Polygon) op.getGeometry();
			// assertEquals( "urn:fes:def:crs:EPSG::4326",
			// polygon.getCoordinateSystem().getName() );
		}
	}

	@Test
	public void parseOGCExample12() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter12.xml")) {
			And and = (And) ((OperatorFilter) filter).getOperator();
		}
	}

	@Test
	public void parseOGCExample13() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter13.xml")) {
			And and = (And) ((OperatorFilter) filter).getOperator();
		}
	}

	@Test
	public void parseOGCExample14() throws XMLStreamException, FactoryConfigurationError, IOException {

		for (Filter filter : getFilters("filter14.xml")) {
			And and = (And) ((OperatorFilter) filter).getOperator();
		}
	}

	@Test
	public void parseBBoxWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		BBOX bbox = (BBOX) parseFilter("v200/bboxWithSpatialJoin.xml");

		assertEquals(((ValueReference) bbox.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(bbox.getGeometry());
		assertNull(bbox.getBoundingBox());
		assertEquals(bbox.getValueReference().getAsText(), "app:ft2/geometry");
	}

	@Test
	public void parseBeyondWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Beyond beyond = (Beyond) parseFilter("v200/beyondWithSpatialJoin.xml");

		assertEquals(((ValueReference) beyond.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(beyond.getGeometry());
		assertEquals(beyond.getValueReference().getAsText(), "app:ft2/geometry");
		assertEquals(beyond.getDistance().getValueAsDouble(), 10d);
	}

	@Test
	public void parseContainsWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Contains contains = (Contains) parseFilter("v200/containsWithSpatialJoin.xml");

		assertEquals(((ValueReference) contains.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(contains.getGeometry());
		assertEquals(contains.getValueReference().getAsText(), "app:ft2/geometry");
	}

	@Test
	public void parseCrossesWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Crosses crosses = (Crosses) parseFilter("v200/crossesWithSpatialJoin.xml");

		assertEquals(((ValueReference) crosses.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(crosses.getGeometry());
		assertEquals(crosses.getValueReference().getAsText(), "app:ft2/geometry");
	}

	@Test
	public void parseDisjointWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Disjoint disjoint = (Disjoint) parseFilter("v200/disjointWithSpatialJoin.xml");

		assertEquals(((ValueReference) disjoint.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(disjoint.getGeometry());
		assertEquals(disjoint.getValueReference().getAsText(), "app:ft2/geometry");
	}

	@Test
	public void parseDWithinWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		DWithin dwithin = (DWithin) parseFilter("v200/dwithinWithSpatialJoin.xml");

		assertEquals(((ValueReference) dwithin.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(dwithin.getGeometry());
		assertEquals(dwithin.getValueReference().getAsText(), "app:ft2/geometry");
		assertEquals(dwithin.getDistance().getValueAsDouble(), 10d);
	}

	@Test
	public void parseEqualsWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Equals equals = (Equals) parseFilter("v200/equalsWithSpatialJoin.xml");

		assertEquals(((ValueReference) equals.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(equals.getGeometry());
		assertEquals(equals.getValueReference().getAsText(), "app:ft2/geometry");
	}

	@Test
	public void parseIntersectsWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Intersects intersects = (Intersects) parseFilter("v200/intersectsWithSpatialJoin.xml");

		assertEquals(((ValueReference) intersects.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(intersects.getGeometry());
		assertEquals(intersects.getValueReference().getAsText(), "app:ft2/geometry");
	}

	@Test
	public void parseOverlapsWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Overlaps overlaps = (Overlaps) parseFilter("v200/overlapsWithSpatialJoin.xml");

		assertEquals(((ValueReference) overlaps.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(overlaps.getGeometry());
		assertEquals(overlaps.getValueReference().getAsText(), "app:ft2/geometry");
	}

	@Test
	public void parseTouchesWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Touches touches = (Touches) parseFilter("v200/touchesWithSpatialJoin.xml");

		assertEquals(((ValueReference) touches.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(touches.getGeometry());
		assertEquals(touches.getValueReference().getAsText(), "app:ft2/geometry");
	}

	@Test
	public void parseWithinWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Within within = (Within) parseFilter("v200/withinWithSpatialJoin.xml");

		assertEquals(((ValueReference) within.getParam1()).getAsText(), "app:ft1/geometry");
		assertNull(within.getGeometry());
		assertEquals(within.getValueReference().getAsText(), "app:ft2/geometry");
	}

	@Test
	public void parsePropertyIsLessThanOrEuqlToWithLiteralContainingUnexpectedGeometry() throws Exception {
		assertThrows(XMLParsingException.class, () -> {
			InputStream filterAsStream = this.getClass().getResourceAsStream("v200/unexectedTestfilter.xml");
			XMLStreamReader xmlStream = XMLInputFactory.newInstance().createXMLStreamReader(filterAsStream);
			XMLStreamUtils.skipStartDocument(xmlStream);
			Filter200XMLDecoder.parse(xmlStream);
		});
	}

	// @Test
	// public void parseOGCExample15()
	// throws XMLStreamException, FactoryConfigurationError, IOException {
	//
	// for ( Filter filter : getFilters( "filter15.xml" ) ) {
	// }
	// }
	//
	// @Test
	// public void parseOGCExample16()
	// throws XMLStreamException, FactoryConfigurationError, IOException {
	//
	// for ( Filter filter : getFilters( "filter16.xml" ) ) {
	// }
	// }
	//
	// @Test
	// public void parseOGCExample17()
	// throws XMLStreamException, FactoryConfigurationError, IOException {
	//
	// for ( Filter filter : getFilters( "filter17.xml" ) ) {
	// }
	// }
	//
	// @Test
	// public void parseOGCExample18()
	// throws XMLStreamException, FactoryConfigurationError, IOException {
	//
	// for ( Filter filter : getFilters( "filter18.xml" ) ) {
	// }
	// }

	private Filter[] getFilters(String name) throws IOException, XMLStreamException, FactoryConfigurationError {
		URL[] urls = getUrls(name);
		Filter[] filters = new Filter[urls.length];
		for (int i = 0; i < urls.length; i++) {
			InputStream is = urls[i].openStream();
			XMLStreamReader xmlStream = XMLInputFactory.newInstance().createXMLStreamReader(is);
			XMLStreamUtils.skipStartDocument(xmlStream);
			filters[i] = Filter200XMLDecoder.parse(xmlStream);
			assertNotNull(filters[i]);
		}
		assertEquals(4, filters.length);
		return filters;
	}

	private URL[] getUrls(String name) {
		URL[] urls = new URL[4];
		urls[0] = buildUrl(name, "2.1.2");
		urls[1] = buildUrl(name, "3.1.1");
		urls[2] = buildUrl(name, "3.2.0");
		urls[3] = buildUrl(name, "3.2.1");
		return urls;
	}

	private URL buildUrl(String name, String version) {
		try {
			String url = new RedirectingEntityResolver().redirect(OGC_EXAMPLES_BASE_URL + version + "/" + name);
			return new URL(url);
		}
		catch (MalformedURLException e) {
			// should never happen
		}
		return null;
	}

	private Operator parseFilter(String resource) throws XMLStreamException, FactoryConfigurationError {
		InputStream filterAsStream = this.getClass().getResourceAsStream(resource);
		XMLStreamReader xmlStream = XMLInputFactory.newInstance().createXMLStreamReader(filterAsStream);
		XMLStreamUtils.skipStartDocument(xmlStream);
		OperatorFilter parsedFilter = (OperatorFilter) Filter200XMLDecoder.parse(xmlStream);
		return parsedFilter.getOperator();
	}

}
