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
package org.deegree.services.csw;

import static org.deegree.commons.xml.CommonNamespaces.XSINS;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.deegree.commons.ows.exception.OWSException;
import org.deegree.commons.tom.ows.Version;
import org.deegree.services.controller.exception.serializer.XMLExceptionSerializer;
import org.deegree.services.controller.utils.HttpResponseBuffer;

/**
 * {@link XMLExceptionSerializer} for CSW 2.0.2 <code>ExceptionReport</code> documents.
 *
 * @author <a href="mailto:tonnhofer@lat-lon.de">Oliver Tonnhofer</a>
 */
public class CswExceptionReportSerializer extends XMLExceptionSerializer {

	private static final String OWS_NS = "http://www.opengis.net/ows";

	private static final String OWS_SCHEMA = "http://schemas.opengis.net/ows/1.0.0/owsExceptionReport.xsd";

	private final Version version;

	/**
	 * Creates a new {@link CswExceptionReportSerializer} instance.
	 * @param version version attribute, must not be <code>null</code>
	 */
	public CswExceptionReportSerializer(Version version) {
		this.version = version;
	}

	@Override
	public void serializeException(HttpResponseBuffer response, OWSException exception)
			throws IOException, XMLStreamException {

		response.reset();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml");
		setExceptionStatusCode(response, exception);
		serializeExceptionToXML(response.getXMLWriter(), exception);
	}

	@Override
	public void serializeExceptionToXML(XMLStreamWriter writer, OWSException ex) throws XMLStreamException {
		if (ex == null || writer == null) {
			return;
		}
		writer.writeStartElement("ows", "ExceptionReport", OWS_NS);
		writer.writeNamespace("ows", OWS_NS);
		writer.writeNamespace("xsi", XSINS);
		writer.writeAttribute(XSINS, "schemaLocation", OWS_NS + " " + OWS_SCHEMA);
		writer.writeAttribute("version", "" + version);
		writer.writeStartElement(OWS_NS, "Exception");
		writer.writeAttribute("exceptionCode", ex.getExceptionCode());
		if (ex.getLocator() != null && !"".equals(ex.getLocator().trim())) {
			writer.writeAttribute("locator", ex.getLocator());
		}
		writer.writeStartElement(OWS_NS, "ExceptionText");
		writer.writeCharacters(ex.getMessage() != null ? ex.getMessage() : "not available");
		writer.writeEndElement();
		writer.writeEndElement(); // Exception
		writer.writeEndElement(); // ExceptionReport
	}

}
