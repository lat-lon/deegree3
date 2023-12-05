/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.protocol.ows.exception;

import org.deegree.commons.ows.exception.OWSException;
import org.deegree.commons.xml.stax.XMLStreamUtils;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link OWSExceptionReader}.
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 */
public class OWSExceptionReaderTest {

	@Test
	public void testOWS100() throws XMLStreamException, FactoryConfigurationError {
		XMLStreamReader xmlReader = getReader("ows100.xml");
		assertTrue(OWSExceptionReader.isExceptionReport(xmlReader.getName()));
		OWSExceptionReport report = OWSExceptionReader.parseExceptionReport(xmlReader);
		assertTrue(xmlReader.isEndElement());
		assertEquals(QName.valueOf("{http://www.opengis.net/ows}ExceptionReport"), xmlReader.getName());
		assertNotNull(report);
		assertEquals(1, report.getExceptions().size());
		OWSException e = report.getExceptions().get(0);
		assertEquals("InvalidRequest", e.getExceptionCode());
		assertEquals("BLABLA", e.getMessage());
		assertEquals(1, e.getMessages().size());
		assertEquals("BLABLA", e.getMessages().get(0));
	}

	@Test
	public void testOWS110() throws XMLStreamException, FactoryConfigurationError {
		XMLStreamReader xmlReader = getReader("ows110.xml");
		assertTrue(OWSExceptionReader.isExceptionReport(xmlReader.getName()));
		OWSExceptionReport report = OWSExceptionReader.parseExceptionReport(xmlReader);
		assertTrue(xmlReader.isEndElement());
		assertEquals(QName.valueOf("{http://www.opengis.net/ows/1.1}ExceptionReport"), xmlReader.getName());
		assertNotNull(report);
		assertEquals(1, report.getExceptions().size());
		OWSException e = report.getExceptions().get(0);
		assertEquals("InvalidRequest", e.getExceptionCode());
		assertEquals("BLABLA", e.getMessage());
		assertEquals(1, e.getMessages().size());
		assertEquals("BLABLA", e.getMessages().get(0));
	}

	@Test
	public void testOWS200() throws XMLStreamException, FactoryConfigurationError {
		XMLStreamReader xmlReader = getReader("ows200.xml");
		assertTrue(OWSExceptionReader.isExceptionReport(xmlReader.getName()));
		OWSExceptionReport report = OWSExceptionReader.parseExceptionReport(xmlReader);
		assertTrue(xmlReader.isEndElement());
		assertEquals(QName.valueOf("{http://www.opengis.net/ows/2.0}ExceptionReport"), xmlReader.getName());
		assertNotNull(report);
		assertEquals("5.6.2", report.getVersion());
		assertEquals(1, report.getExceptions().size());
		OWSException e = report.getExceptions().get(0);
		assertEquals("InvalidRequest", e.getExceptionCode());
		assertEquals("BLABLA", e.getMessage());
		assertEquals(1, e.getMessages().size());
		assertEquals("BLABLA", e.getMessages().get(0));
	}

	@Test
	public void testOWS100MultiMessages() throws XMLStreamException, FactoryConfigurationError {
		XMLStreamReader xmlReader = getReader("ows100_multi.xml");
		assertTrue(OWSExceptionReader.isExceptionReport(xmlReader.getName()));
		OWSExceptionReport report = OWSExceptionReader.parseExceptionReport(xmlReader);
		assertTrue(xmlReader.isEndElement());
		assertEquals(QName.valueOf("{http://www.opengis.net/ows}ExceptionReport"), xmlReader.getName());
		assertNotNull(report);
		assertEquals(2, report.getExceptions().size());
		OWSException e = report.getExceptions().get(0);
		assertEquals("InvalidRequest", e.getExceptionCode());
		assertEquals("BLABLA;ARGL", e.getMessage());
		assertEquals(2, e.getMessages().size());
		assertEquals("BLABLA", e.getMessages().get(0));
		assertEquals("ARGL", e.getMessages().get(1));
	}

	@Test
	public void testWFS100() throws XMLStreamException, FactoryConfigurationError {
		XMLStreamReader xmlReader = getReader("wfs100.xml");
		assertTrue(OWSExceptionReader.isExceptionReport(xmlReader.getName()));
		OWSExceptionReport report = OWSExceptionReader.parseExceptionReport(xmlReader);
		assertTrue(xmlReader.isEndElement());
		assertEquals(QName.valueOf("{http://www.opengis.net/ogc}ServiceExceptionReport"), xmlReader.getName());
		assertNotNull(report);
	}

	@Test
	public void testWMS100() throws XMLStreamException, FactoryConfigurationError {
		XMLStreamReader xmlReader = getReader("wms111.xml");
		assertTrue(OWSExceptionReader.isExceptionReport(xmlReader.getName()));
		OWSExceptionReport report = OWSExceptionReader.parseExceptionReport(xmlReader);
		assertTrue(xmlReader.isEndElement());
		assertEquals(QName.valueOf("ServiceExceptionReport"), xmlReader.getName());
		assertNotNull(report);
	}

	@Test
	public void testWFSCapabilities100() throws XMLStreamException, FactoryConfigurationError {
		XMLStreamReader xmlReader = getReader("wfs_capabilities100.xml");
		assertFalse(OWSExceptionReader.isExceptionReport(xmlReader.getName()));
		OWSExceptionReport report = OWSExceptionReader.parseExceptionReport(xmlReader);
		assertTrue(xmlReader.isEndElement());
		assertEquals(QName.valueOf("{http://www.opengis.net/wfs}WFS_Capabilities"), xmlReader.getName());
		assertNotNull(report);
	}

	private XMLStreamReader getReader(String name) throws XMLStreamException, FactoryConfigurationError {
		InputStream is = OWSExceptionReaderTest.class.getResourceAsStream(name);
		XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(is);
		XMLStreamUtils.skipStartDocument(xmlReader);
		return xmlReader;
	}

}
