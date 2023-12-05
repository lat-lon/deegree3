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
package org.deegree.protocol.wms.map;

import org.deegree.commons.tom.datetime.DateTime;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.geometry.Envelope;
import org.deegree.layer.LayerRef;
import org.deegree.layer.dims.DimensionInterval;
import org.deegree.protocol.wms.ops.GetMap;
import org.deegree.style.StyleRef;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class GetMapParserTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testParse() throws Exception {
		GetMapParser getMapXMLAdapter = new GetMapParser();
		XMLStreamReader xmlStreamReader = createXmlStreamReader("wms-1.3.0-GetMap.xml");
		GetMap getMap = getMapXMLAdapter.parse(xmlStreamReader);

		LinkedList<LayerRef> layers = getMap.getLayers();
		assertEquals(layers.size(), 3);
		assertThat(layers, hasLayerRef("municipalities"));
		assertThat(layers, hasLayerRef("counties"));
		assertThat(layers, hasLayerRef("zipcodes"));

		LinkedList<StyleRef> styles = getMap.getStyles();
		assertEquals(styles.size(), 3);
		assertThat(styles, hasStyleRef("Municipalities"));
		assertThat(styles, hasStyleRef("CountyBoundary"));
		assertThat(styles, hasStyleRef("default"));

		assertEquals(getMap.getWidth(), 1024);
		assertEquals(getMap.getHeight(), 512);
		assertEquals(getMap.getFormat(), "image/png");
		assertEquals(getMap.getTransparent(), true);
		assertEquals(getMap.getBgColor(), BLACK);

		assertEquals(getMap.getCoordinateSystem(), CRSManager.lookup("EPSG:4326"));
		Envelope boundingBox = getMap.getBoundingBox();
		assertEquals(boundingBox.getMin().get0(), -115.4);
		assertEquals(boundingBox.getMin().get1(), 35.0);
		assertEquals(boundingBox.getMax().get0(), -108.0);
		assertEquals(boundingBox.getMax().get1(), 44.0);

		Map<String, String> parameterMap = getMap.getParameterMap();
		assertEquals(parameterMap.size(), 1);
		assertEquals(parameterMap.get("EXCEPTIONS"), "INIMAGE");

		HashMap<String, List<?>> dimensions = getMap.getDimensions();
		assertEquals(dimensions.size(), 2);

		assertEquals(((List<DateTime>) dimensions.get("time")).size(), 1);
		assertEquals(((List<DateTime>) dimensions.get("time")).get(0).getDate(), expectedDateTime());

		assertEquals(((List<Double>) dimensions.get("elevation")).size(), 1);
		assertEquals(((List<Double>) dimensions.get("elevation")).get(0), 5d);
	}

	@Test
	public void testParse_defaultValues() throws Exception {
		GetMapParser getMapXMLAdapter = new GetMapParser();
		XMLStreamReader xmlStreamReader = createXmlStreamReader("wms-1.3.0-GetMap_simple.xml");
		GetMap getMap = getMapXMLAdapter.parse(xmlStreamReader);

		LinkedList<LayerRef> layers = getMap.getLayers();
		assertEquals(layers.size(), 1);
		assertThat(layers, hasLayerRef("municipalities"));

		LinkedList<StyleRef> styles = getMap.getStyles();
		assertEquals(styles.size(), 1);
		assertThat(styles, hasStyleRef("Municipalities"));

		assertEquals(getMap.getWidth(), 10);
		assertEquals(getMap.getHeight(), 50);
		assertEquals(getMap.getFormat(), "image/jpeg");
		assertEquals(getMap.getTransparent(), false);
		assertEquals(getMap.getBgColor(), WHITE);

		assertEquals(getMap.getCoordinateSystem(), CRSManager.lookup("EPSG:4326"));
		Envelope boundingBox = getMap.getBoundingBox();
		assertEquals(boundingBox.getMin().get0(), -115.4);
		assertEquals(boundingBox.getMin().get1(), 35.0);
		assertEquals(boundingBox.getMax().get0(), -108.0);
		assertEquals(boundingBox.getMax().get1(), 44.0);

		Map<String, String> parameterMap = getMap.getParameterMap();
		assertEquals(parameterMap.size(), 1);
		assertEquals(parameterMap.get("EXCEPTIONS"), "XML");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testParse_elevationValues() throws Exception {
		GetMapParser getMapXMLAdapter = new GetMapParser();
		XMLStreamReader xmlStreamReader = createXmlStreamReader("wms-1.3.0-GetMap_elevationValues.xml");
		GetMap getMap = getMapXMLAdapter.parse(xmlStreamReader);

		HashMap<String, List<?>> dimensions = getMap.getDimensions();
		assertEquals(dimensions.size(), 1);

		List<Double> elevationValues = (List<Double>) dimensions.get("elevation");
		assertEquals(elevationValues.size(), 5);
		assertThat(elevationValues, hasItems(-1.5, -0.5, 0d, 0.5, 1.5));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testParse_elevationInterval() throws Exception {
		GetMapParser getMapXMLAdapter = new GetMapParser();
		XMLStreamReader xmlStreamReader = createXmlStreamReader("wms-1.3.0-GetMap_elevationInterval.xml");
		GetMap getMap = getMapXMLAdapter.parse(xmlStreamReader);

		HashMap<String, List<?>> dimensions = getMap.getDimensions();
		assertEquals(dimensions.size(), 1);

		List<DimensionInterval<Double, Double, Double>> elevationValues = (List<DimensionInterval<Double, Double, Double>>) dimensions
			.get("elevation");
		assertEquals(elevationValues.size(), 1);
		assertEquals(elevationValues.get(0).min, -5d);
		assertEquals(elevationValues.get(0).max, 5d);
		assertEquals(elevationValues.get(0).res, 0d);
	}

	private XMLStreamReader createXmlStreamReader(String resource)
			throws XMLStreamException, FactoryConfigurationError {
		InputStream getMapResource = GetMapParserTest.class.getResourceAsStream(resource);
		return XMLInputFactory.newInstance().createXMLStreamReader(getMapResource);
	}

	private Date expectedDateTime() {
		Calendar expectedCalendar = Calendar.getInstance();
		expectedCalendar.set(Calendar.YEAR, 2015);
		expectedCalendar.set(Calendar.MONTH, 7);
		expectedCalendar.set(Calendar.DAY_OF_MONTH, 24);
		expectedCalendar.set(Calendar.HOUR_OF_DAY, 9);
		expectedCalendar.set(Calendar.MINUTE, 30);
		expectedCalendar.set(Calendar.SECOND, 0);
		expectedCalendar.set(Calendar.MILLISECOND, 0);
		expectedCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		return expectedCalendar.getTime();
	}

	@SuppressWarnings("unchecked")
	private Matcher<LinkedList<LayerRef>> hasLayerRef(final String layer) {
		return new BaseMatcher<LinkedList<LayerRef>>() {

			@Override
			public boolean matches(Object item) {
				LinkedList<LayerRef> layers = (LinkedList<LayerRef>) item;
				for (LayerRef layerRef : layers) {
					if (layer.equals(layerRef.getName()))
						return true;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("List should contain a layer with name " + layer);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private Matcher<LinkedList<StyleRef>> hasStyleRef(final String style) {
		return new BaseMatcher<LinkedList<StyleRef>>() {

			@Override
			public boolean matches(Object item) {
				LinkedList<StyleRef> styles = (LinkedList<StyleRef>) item;
				for (StyleRef styleRef : styles) {
					if (style.equals(styleRef.getName()))
						return true;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("List should contain a style with name " + style);
			}
		};
	}

}