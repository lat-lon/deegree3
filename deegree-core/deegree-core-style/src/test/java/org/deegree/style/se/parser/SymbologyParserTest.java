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

package org.deegree.style.se.parser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * <code>SymbologyParserTest</code>
 *
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 */
// TODO: fix dependencies
@Disabled
public class SymbologyParserTest {

	private static final Logger LOG = getLogger(SymbologyParserTest.class);

	@ParameterizedTest
	@MethodSource("getFiles")
	public void singleTest(String testlabel, String file) throws XMLStreamException, FactoryConfigurationError {
		final XMLInputFactory fac = XMLInputFactory.newInstance();
		final Class<SymbologyParserTest> cls = SymbologyParserTest.class;

		if (file.endsWith(".xml")) {
			LOG.debug("Expecting {} to parse fine.", file);
			XMLStreamReader in = fac.createXMLStreamReader(cls.getResource(file).toString(),
					cls.getResourceAsStream(file));
			in.next();
			assertNotNull(SymbologyParser.INSTANCE.parse(in));
		}
		if (file.endsWith(".bad")) {
			LOG.debug("Expecting {} to fail.", file);
			XMLStreamReader in = fac.createXMLStreamReader(cls.getResource(file).toString(),
					cls.getResourceAsStream(file));
			in.next();
			try {
				SymbologyParser.INSTANCE.parse(in);
				assertEquals(true, false);
			}
			catch (XMLStreamException e) {
				assertNotNull(e);
			}
		}
	}

	private static Stream<Arguments> getFiles() {
		return Stream.of(Arguments.of("setest1.bad", "setest1.bad"), Arguments.of("setest1.xml", "setest1.xml"),
				Arguments.of("setest10.xml", "setest10.xml"), Arguments.of("setest11.xml", "setest11.xml"),
				Arguments.of("setest12.xml", "setest12.xml"), Arguments.of("setest13.xml", "setest13.xml"),
				Arguments.of("setest14.xml", "setest14.xml"), Arguments.of("setest15.xml", "setest15.xml"),
				Arguments.of("setest16.xml", "setest16.xml"), Arguments.of("setest17.xml", "setest17.xml"),
				Arguments.of("setest18.xml", "setest18.xml"), Arguments.of("setest19.xml", "setest19.xml"),
				Arguments.of("setest2.xml", "setest2.xml"), Arguments.of("setest20.xml", "setest20.xml"),
				Arguments.of("setest21.xml", "setest21.xml"), Arguments.of("setest22.xml", "setest22.xml"),
				Arguments.of("setest3.xml", "setest3.xml"), Arguments.of("setest4.xml", "setest4.xml"),
				Arguments.of("setest5.xml", "setest5.xml"), Arguments.of("setest6.xml", "setest6.xml"),
				Arguments.of("setest7.xml", "setest7.xml"), Arguments.of("setest8.xml", "setest8.xml"),
				Arguments.of("setest9.xml", "setest9.xml"), Arguments.of("sldtest1.bad", "sldtest1.bad"),
				Arguments.of("sldtest1.xml", "sldtest1.xml"), Arguments.of("sldtest10.xml", "sldtest10.xml"),
				Arguments.of("sldtest2.xml", "sldtest2.xml"), Arguments.of("sldtest3.xml", "sldtest3.xml"),
				Arguments.of("sldtest4.xml", "sldtest4.xml"), Arguments.of("sldtest5.xml", "sldtest5.xml"),
				Arguments.of("sldtest6.xml", "sldtest6.xml"), Arguments.of("sldtest7.xml", "sldtest7.xml"),
				Arguments.of("sldtest8.xml", "sldtest8.xml"), Arguments.of("sldtest9.xml", "sldtest9.xml"),
				Arguments.of("lineplacement_ext.xml", "lineplacement_ext.xml"));
	}

}
