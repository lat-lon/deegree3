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

import org.deegree.commons.xml.XMLParsingException;
import org.deegree.cs.exceptions.TransformationException;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.filter.Filter;
import org.deegree.filter.IdFilter;
import org.deegree.filter.Operator;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.comparison.ComparisonOperator;
import org.deegree.filter.expression.ValueReference;
import org.deegree.filter.logical.And;
import org.deegree.filter.logical.LogicalOperator;
import org.deegree.filter.spatial.BBOX;
import org.deegree.filter.spatial.Contains;
import org.deegree.filter.spatial.Crosses;
import org.deegree.filter.spatial.Disjoint;
import org.deegree.filter.spatial.Equals;
import org.deegree.filter.spatial.Intersects;
import org.deegree.filter.spatial.Overlaps;
import org.deegree.filter.spatial.Touches;
import org.deegree.filter.spatial.Within;
import org.deegree.junit.XMLAssert;
import org.deegree.junit.XMLMemoryStreamWriter;
import org.deegree.workspace.standard.DefaultWorkspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Tests the correct parsing and exporting of Filter Encoding 1.1.0 documents.
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 */
public class Filter110XMLDecoderTest {

	private static final Logger LOG = getLogger(Filter110XMLDecoderTest.class);

	@BeforeEach
	public void setUp() throws Exception {
		new DefaultWorkspace(new File("nix")).initAll();
	}

	@Test
	public void parseIdFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter_110_id.xml");
		assertNotNull(filter);
		assertEquals(Filter.Type.ID_FILTER, filter.getType());
		IdFilter idFilter = (IdFilter) filter;
		assertEquals(4, idFilter.getMatchingIds().size());
		assertTrue(idFilter.getMatchingIds().contains("PHILOSOPHER_966"));
		assertTrue(idFilter.getMatchingIds().contains("PHILOSOPHER_967"));
		assertTrue(idFilter.getMatchingIds().contains("PHILOSOPHER_968"));
		assertTrue(idFilter.getMatchingIds().contains("PHILOSOPHER_969"));
	}

	@Test
	public void parseMixedIdFilter() throws FactoryConfigurationError {
		assertThrows(XMLParsingException.class, () -> {
			parse("testfilter_110_id_mixed.xml");
		});
	}

	@Test
	public void parseOperatorFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter_110_operator.xml");
		assertNotNull(filter);
		assertEquals(Filter.Type.OPERATOR_FILTER, filter.getType());
		OperatorFilter operatorFilter = (OperatorFilter) filter;
		assertEquals(Operator.Type.LOGICAL, operatorFilter.getOperator().getType());
		LogicalOperator logicalOperator = (LogicalOperator) operatorFilter.getOperator();
		assertEquals(LogicalOperator.SubType.AND, logicalOperator.getSubType());
		And and = (And) logicalOperator;
		assertEquals(2, and.getSize());
		assertEquals(Operator.Type.COMPARISON, and.getParameter(0).getType());
		ComparisonOperator param1Oper = (ComparisonOperator) and.getParameter(0);
		assertEquals(ComparisonOperator.SubType.PROPERTY_IS_GREATER_THAN, param1Oper.getSubType());
		assertEquals(Operator.Type.COMPARISON, and.getParameter(1).getType());
		ComparisonOperator param2Oper = (ComparisonOperator) and.getParameter(1);
		assertEquals(ComparisonOperator.SubType.PROPERTY_IS_EQUAL_TO, param2Oper.getSubType());

	}

	@Test
	public void parseBrokenIdFilterDocument() throws FactoryConfigurationError {
		assertThrows(XMLParsingException.class, () -> {
			parse("testfilter_110_id.invalid_xml");
		});
	}

	@Test
	public void parseBrokenIdFilterDocument2() throws FactoryConfigurationError {
		assertThrows(XMLParsingException.class, () -> {
			parse("testfilter_110_id2.invalid_xml");
		});
	}

	@Test
	public void parseAndExportFilterDocument() throws XMLStreamException, FactoryConfigurationError, IOException,
			UnknownCRSException, TransformationException {

		Filter filter = parse("testfilter_110_operator.xml");

		XMLMemoryStreamWriter writer = new XMLMemoryStreamWriter();
		Filter110XMLEncoder.export(filter, writer.getXMLStreamWriter());

		String schemaLocation = "http://schemas.opengis.net/filter/1.1.0/filter.xsd";
		XMLAssert.assertValidity(writer.getReader(), schemaLocation);
	}

	private Filter parse(String resourceName) throws XMLStreamException, FactoryConfigurationError, IOException {
		URL url = Filter110XMLDecoderTest.class.getResource("v110/" + resourceName);
		XMLStreamReader xmlStream = XMLInputFactory.newInstance()
			.createXMLStreamReader(url.toString(), url.openStream());
		xmlStream.nextTag();
		Location loc = xmlStream.getLocation();
		LOG.debug("" + loc.getLineNumber());
		LOG.debug("" + loc.getSystemId());
		LOG.debug("" + loc.getColumnNumber());
		return Filter110XMLDecoder.parse(xmlStream);
	}

	@Test
	public void parseBeyondFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter15.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseDisjointFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter16.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseContainsFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter17.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseCrossesFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter18.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseDWithinFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter19.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseIntersectsFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter20.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseEqualsFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter21.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseOverlapsFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter22.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseTouchesFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter23.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseWithinFilter() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("testfilter24.xml");
		assertNotNull(filter);

	}

	@Test
	public void parseBBoxWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("bboxWithSpatialJoin.xml");
		BBOX bbox = (BBOX) ((OperatorFilter) filter).getOperator();

		assertEquals(((ValueReference) bbox.getParam1()).getAsText(), "app:ft1/app:geom");
		assertNull(bbox.getGeometry());
		assertEquals(bbox.getValueReference().getAsText(), "app:ft2/app:geom");
	}

	@Test
	public void parseContainsWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("containsWithSpatialJoin.xml");
		Contains contains = (Contains) ((OperatorFilter) filter).getOperator();

		assertEquals(((ValueReference) contains.getParam1()).getAsText(), "app:ft1/app:geom");
		assertNull(contains.getGeometry());
		assertEquals(contains.getValueReference().getAsText(), "app:ft2/app:geom");
	}

	@Test
	public void parseCrossesWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("crossesWithSpatialJoin.xml");
		Crosses crosses = (Crosses) ((OperatorFilter) filter).getOperator();

		assertEquals(((ValueReference) crosses.getParam1()).getAsText(), "app:ft1/app:geom");
		assertNull(crosses.getGeometry());
		assertEquals(crosses.getValueReference().getAsText(), "app:ft2/app:geom");
	}

	@Test
	public void parseDisjointWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("disjointWithSpatialJoin.xml");
		Disjoint disjoint = (Disjoint) ((OperatorFilter) filter).getOperator();

		assertEquals(((ValueReference) disjoint.getParam1()).getAsText(), "app:ft1/app:geom");
		assertNull(disjoint.getGeometry());
		assertEquals(disjoint.getValueReference().getAsText(), "app:ft2/app:geom");
	}

	@Test
	public void parseEqualsWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("equalsWithSpatialJoin.xml");
		Equals equals = (Equals) ((OperatorFilter) filter).getOperator();

		assertEquals(((ValueReference) equals.getParam1()).getAsText(), "app:ft1/app:geom");
		assertNull(equals.getGeometry());
		assertEquals(equals.getValueReference().getAsText(), "app:ft2/app:geom");
	}

	@Test
	public void parseIntersectsWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("intersectsWithSpatialJoin.xml");
		Intersects intersects = (Intersects) ((OperatorFilter) filter).getOperator();

		assertEquals(((ValueReference) intersects.getParam1()).getAsText(), "app:ft1/app:geom");
		assertNull(intersects.getGeometry());
		assertEquals(intersects.getValueReference().getAsText(), "app:ft2/app:geom");
	}

	@Test
	public void parseOverlapsWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("overlapsWithSpatialJoin.xml");
		Overlaps overlaps = (Overlaps) ((OperatorFilter) filter).getOperator();

		assertEquals(((ValueReference) overlaps.getParam1()).getAsText(), "app:ft1/app:geom");
		assertNull(overlaps.getGeometry());
		assertEquals(overlaps.getValueReference().getAsText(), "app:ft2/app:geom");
	}

	@Test
	public void parseTouchesWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("touchesWithSpatialJoin.xml");
		Touches touches = (Touches) ((OperatorFilter) filter).getOperator();

		assertEquals(((ValueReference) touches.getParam1()).getAsText(), "app:ft1/app:geom");
		assertNull(touches.getGeometry());
		assertEquals(touches.getValueReference().getAsText(), "app:ft2/app:geom");
	}

	@Test
	public void parseWithinWithSpatialJoin() throws XMLStreamException, FactoryConfigurationError, IOException {
		Filter filter = parse("withinWithSpatialJoin.xml");
		Within within = (Within) ((OperatorFilter) filter).getOperator();

		assertEquals(((ValueReference) within.getParam1()).getAsText(), "app:ft1/app:geom");
		assertNull(within.getGeometry());
		assertEquals(within.getValueReference().getAsText(), "app:ft2/app:geom");
	}

}