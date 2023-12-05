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

 Occam Labs UG (haftungsbeschränkt)
 Godesberger Allee 139, 53175 Bonn
 Germany
 http://www.occamlabs.de/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/

package org.deegree.tile.tilematrixset;

import org.deegree.workspace.Workspace;
import org.deegree.workspace.standard.DefaultWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * <code>TileMatrixSetManagerTest</code>
 *
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 */

public class TileMatrixSetManagerTest {

	private Workspace workspace;

	@BeforeEach
	public void setup() {
		workspace = new DefaultWorkspace(new File("nix"));
		workspace.initAll();
	}

	@AfterEach
	public void shutdown() {
		workspace.destroy();
	}

	@Test
	public void testWellKnownScaleSets() {
		assertNotNull(workspace.getResource(TileMatrixSetProvider.class, "globalcrs84pixel"),
				"globalcrs84pixel not defined.");
		assertNotNull(workspace.getResource(TileMatrixSetProvider.class, "globalcrs84scale"),
				"globalcrs84scale not defined.");
		assertNotNull(workspace.getResource(TileMatrixSetProvider.class, "googlecrs84quad"),
				"googlecrs84quad not defined.");
		assertNotNull(workspace.getResource(TileMatrixSetProvider.class, "googlemapscompatible"),
				"googlemapscompatible not defined.");
		assertNotNull(workspace.getResource(TileMatrixSetProvider.class, "inspirecrs84quad"),
				"inspirecrs84quad not defined.");
	}

}
