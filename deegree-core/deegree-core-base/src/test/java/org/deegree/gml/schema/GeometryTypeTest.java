/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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
package org.deegree.gml.schema;

import org.deegree.feature.types.property.GeometryPropertyType.GeometryType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.COMPOSITE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.COMPOSITE_CURVE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.COMPOSITE_SOLID;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.COMPOSITE_SURFACE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.CURVE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.GEOMETRY;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.LINEAR_RING;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.LINE_STRING;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.MULTI_CURVE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.MULTI_POINT;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.MULTI_SOLID;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.MULTI_SURFACE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.ORIENTABLE_SURFACE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.POINT;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.POLYGON;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.PRIMITIVE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.RING;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.SOLID;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.SURFACE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.TRIANGULATED_SURFACE;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.determineMinimalBaseGeometry;
import static org.deegree.feature.types.property.GeometryPropertyType.GeometryType.fromGMLTypeName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests some of the geometrytype mapping functions.
 *
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 */
public class GeometryTypeTest {

	/**
	 * Grid is not known
	 */
	@Test
	public void testGrid() {
		assertThrows(IllegalArgumentException.class, () -> {
			fromGMLTypeName("Grid");
		});
	}

	/**
	 * Rectifiedgrid is not known
	 */
	@Test
	public void testRectifiedGrid() {
		assertThrows(IllegalArgumentException.class, () -> {
			fromGMLTypeName("RectifiedGrid");
		});
	}

	/**
	 * Test some incoming names
	 */
	@Test
	public void mapFromString() {
		assertEquals(GEOMETRY, fromGMLTypeName("_Geometry"));
		assertEquals(MULTI_CURVE, fromGMLTypeName("MultiCurve"));
		assertEquals(MULTI_POINT, fromGMLTypeName("MultiPoint"));
		assertEquals(MULTI_SOLID, fromGMLTypeName("MultiSolid"));
		assertEquals(MULTI_SURFACE, fromGMLTypeName("MultiSurface"));
		assertEquals(POINT, fromGMLTypeName("Point"));
		assertEquals(POLYGON, fromGMLTypeName("Polygon"));
		assertEquals(SOLID, fromGMLTypeName("_Solid"));
		assertEquals(SURFACE, fromGMLTypeName("_Surface"));
		assertEquals(TRIANGULATED_SURFACE, fromGMLTypeName("TriangulatedSurface"));

		assertEquals(GEOMETRY, fromGMLTypeName("AbstractGeometry"));
		assertEquals(SOLID, fromGMLTypeName("AbstractSolid"));
		assertEquals(SURFACE, fromGMLTypeName("AbstractSurface"));
	}

	/**
	 * Find the parents of two different geometry types
	 */
	@Test
	public void findParents() {
		assertEquals(CURVE, LINEAR_RING.findCommonBaseType(COMPOSITE_CURVE));
		assertEquals(CURVE, COMPOSITE_CURVE.findCommonBaseType(LINEAR_RING));
		assertEquals(PRIMITIVE, POINT.findCommonBaseType(COMPOSITE_SOLID));
		assertEquals(PRIMITIVE, COMPOSITE_SOLID.findCommonBaseType(POINT));

		assertEquals(RING, RING.findCommonBaseType(LINEAR_RING));
		assertEquals(RING, LINEAR_RING.findCommonBaseType(RING));

		assertEquals(GEOMETRY, MULTI_CURVE.findCommonBaseType(COMPOSITE));

		assertEquals(GEOMETRY, COMPOSITE.findCommonBaseType(MULTI_CURVE));

		assertEquals(SURFACE, ORIENTABLE_SURFACE.findCommonBaseType(COMPOSITE_SURFACE));

		assertEquals(SURFACE, COMPOSITE_SURFACE.findCommonBaseType(ORIENTABLE_SURFACE));

		assertEquals(ORIENTABLE_SURFACE, ORIENTABLE_SURFACE.findCommonBaseType(ORIENTABLE_SURFACE));

		assertEquals(GEOMETRY, ORIENTABLE_SURFACE.findCommonBaseType(GEOMETRY));
		assertEquals(GEOMETRY, GEOMETRY.findCommonBaseType(ORIENTABLE_SURFACE));

	}

	/**
	 * Find the base of different geometry types
	 */
	@Test
	public void findBaseGeometry() {
		HashSet<GeometryType> set = new HashSet<GeometryType>();
		set.add(LINEAR_RING);
		assertEquals(LINEAR_RING, determineMinimalBaseGeometry(set));

		set.add(RING);
		assertEquals(RING, determineMinimalBaseGeometry(set));

		set.add(COMPOSITE_CURVE);
		set.add(LINE_STRING);
		assertEquals(CURVE, determineMinimalBaseGeometry(set));

		set.add(COMPOSITE_SOLID);
		set.add(POINT);
		assertEquals(PRIMITIVE, determineMinimalBaseGeometry(set));

		set.add(MULTI_CURVE);
		assertEquals(GEOMETRY, determineMinimalBaseGeometry(set));
	}

}
