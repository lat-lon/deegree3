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
package org.deegree.protocol.wms.ops;

import org.deegree.commons.utils.Pair;
import org.deegree.commons.utils.Triple;
import org.deegree.filter.OperatorFilter;
import org.deegree.layer.LayerRef;
import org.deegree.protocol.wms.sld.StyleContainer;
import org.deegree.protocol.wms.sld.StylesContainer;
import org.deegree.style.StyleRef;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

/**
 * TODO add class documentation here
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class SLDParserTest {

	@Test
	public void testParse() throws Exception {
		XMLStreamReader in = retrieveSldAsStream("example-sld.xml");
		RequestBase gm = mockRequestBase();
		Triple<LinkedList<LayerRef>, LinkedList<StyleRef>, LinkedList<OperatorFilter>> sld = SLDParser.parse(in, gm);

		LinkedList<LayerRef> layerRefs = sld.first;
		assertEquals(layerRefs.size(), 1);
		assertEquals(layerRefs.get(0).getName(), "OCEANSEA_1M:Foundation");

		LinkedList<StyleRef> styleRefs = sld.second;
		assertEquals(styleRefs.size(), 1);
		assertEquals(styleRefs.get(0).getStyle().getFeatureType(), new QName("Foundation"));
		assertEquals(styleRefs.get(0).getStyle().getRules().size(), 1);

		LinkedList<OperatorFilter> operatorFilters = sld.third;
		assertEquals(operatorFilters.size(), 1);
		assertNull(operatorFilters.get(0));
	}

	@Test
	public void testParse_StyleInformations() throws Exception {
		XMLStreamReader in = retrieveSldAsStream("example-sld.xml");
		StylesContainer sld = SLDParser.parse(in);
		List<Pair<String, List<?>>> dimensions = sld.getDimensions();
		assertEquals(dimensions.size(), 0);

		List<StyleContainer> styles = sld.getStyles();
		assertEquals(styles.size(), 1);

		StyleContainer styleInformation = styles.get(0);

		LayerRef layerRefs = styleInformation.getLayerRef();
		assertEquals(layerRefs.getName(), "OCEANSEA_1M:Foundation");

		StyleRef layerRef = styleInformation.getStyleRef();
		assertEquals(layerRef.getStyle().getFeatureType(), new QName("Foundation"));
		assertEquals(layerRef.getStyle().getRules().size(), 1);

		OperatorFilter filter = styleInformation.getFilter();
		assertNull(filter);
	}

	private XMLStreamReader retrieveSldAsStream(String resource) throws XMLStreamException, FactoryConfigurationError {
		InputStream resourceAsStream = SLDParserTest.class.getResourceAsStream(resource);
		return XMLInputFactory.newInstance().createXMLStreamReader(resourceAsStream);
	}

	private RequestBase mockRequestBase() {
		return mock(RequestBase.class);
	}

}
