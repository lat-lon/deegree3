/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.cs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for CRSCodeType
 *
 * @author <a href="mailto:ionita@lat-lon.de">Andrei Ionita</a>
 *
 */
public class CRSCodeTypeTest {

	@Test
	public void test_urn_x_ogc() {
		CRSCodeType code = new CRSCodeType("URN:X-OGC:DEF:CRS:EPSG:6.11:4326");
		assertEquals(code.toString(), "epsg:6.11:4326");
		assertEquals(code.getCode(), "4326");
		assertEquals(code.getCodeVersion(), "6.11");
		assertEquals(code.getCodeSpace(), "epsg");
	}

	@Test
	public void test_urn_x_ogc_3PartVersion() {
		CRSCodeType code = new CRSCodeType("URN:X-OGC:DEF:CRS:EPSG:6.11.2:4326");
		assertEquals(code.toString(), "epsg:6.11.2:4326");
		assertEquals(code.getCode(), "4326");
		assertEquals(code.getCodeVersion(), "6.11.2");
		assertEquals(code.getCodeSpace(), "epsg");
	}

	@Test
	public void test_http_opengis_gml() {
		CRSCodeType code = new CRSCodeType("HTTP://WWW.OPENGIS.NET/GML/SRS/EPSG.XML#4326");
		assertEquals(code.toString(), "epsg:4326");
		assertEquals(code.getCode(), "4326");
		assertEquals(code.getCodeVersion(), "");
		assertEquals(code.getCodeSpace(), "epsg");
	}

	@Test
	public void test_urn_opengis() {
		CRSCodeType code = new CRSCodeType("URN:OPENGIS:DEF:CRS:EPSG::4326");
		assertEquals(code.toString(), "epsg:4326");
		assertEquals(code.getCode(), "4326");
		assertEquals(code.getCodeVersion(), "");
		assertEquals(code.getCodeSpace(), "epsg");
	}

	@Test
	public void test_crs() {
		CRSCodeType code = new CRSCodeType("CRS:84");
		assertEquals(code.toString(), "CRS:84");
		assertEquals(code.getCode(), "");
		assertEquals(code.getCodeVersion(), "");
		assertEquals(code.getCodeSpace(), "");
	}

	@Test
	public void test_urn_ogc() {
		CRSCodeType code = new CRSCodeType("URN:OGC:DEF:CRS:OGC:1.3:CRS84");
		assertEquals(code.toString(), "URN:OGC:DEF:CRS:OGC:1.3:CRS84");
		assertEquals(code.getCode(), "");
		assertEquals(code.getCodeVersion(), "");
		assertEquals(code.getCodeSpace(), "");
	}

	@Test
	public void test_wgs84() {
		CRSCodeType code = new CRSCodeType("WGS84(DD)");
		assertEquals(code.toString(), "WGS84(DD)");
		assertEquals(code.getCode(), "");
		assertEquals(code.getCodeVersion(), "");
		assertEquals(code.getCodeSpace(), "");
	}

	@Test
	public void test_http_opengis_def() {
		CRSCodeType code = new CRSCodeType("HTTP://WWW.OPENGIS.NET/DEF/CRS/EPSG/0/4326");
		assertEquals(code.toString(), "epsg:0:4326");
		assertEquals(code.getCode(), "4326");
		assertEquals(code.getCodeVersion(), "0");
		assertEquals(code.getCodeSpace(), "epsg");
	}

}
