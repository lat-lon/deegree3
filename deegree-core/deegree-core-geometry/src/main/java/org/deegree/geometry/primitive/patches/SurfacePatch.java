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
package org.deegree.geometry.primitive.patches;

import org.deegree.commons.uom.Measure;
import org.deegree.commons.uom.Unit;
import org.deegree.geometry.primitive.Surface;

/**
 * A {@link SurfacePatch} describes a continuous portion of a {@link Surface}.
 *
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 *
 */
public interface SurfacePatch {

	/**
	 * Convenience enum type for discriminating the different surface patch variants in
	 * switch statements.
	 */
	public enum SurfacePatchType {

		/** Patch is a {@link GriddedSurfacePatch}. */
		GRIDDED_SURFACE_PATCH,
		/** Patch is a {@link PolygonPatch}. */
		POLYGON_PATCH

	}

	/**
	 * Returns the type of surface patch.
	 * @return the type of surface patch
	 */
	public SurfacePatchType getSurfacePatchType();

	/**
	 * Returns the coordinate dimension, i.e. the dimension of the space that the patch is
	 * embedded in.
	 * @return the coordinate dimension
	 */
	public int getCoordinateDimension();

	/**
	 * Returns the area covered by the patch.
	 * @param uom
	 * @return area covered by the patch in the requested uom
	 */
	public Measure getArea(Unit uom);

}