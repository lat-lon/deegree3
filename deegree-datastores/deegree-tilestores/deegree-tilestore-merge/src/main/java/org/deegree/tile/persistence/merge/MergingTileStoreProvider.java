/*----------------------------------------------------------------------------
 This file is part of deegree
 Copyright (C) 2001-2013 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -
 and
 - Occam Labs UG (haftungsbeschränkt) -
 and others

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

 e-mail: info@deegree.org
 website: http://www.deegree.org/
----------------------------------------------------------------------------*/
package org.deegree.tile.persistence.merge;

import java.net.URL;

import org.deegree.tile.persistence.TileStore;
import org.deegree.tile.persistence.TileStoreProvider;
import org.deegree.workspace.ResourceLocation;
import org.deegree.workspace.ResourceMetadata;
import org.deegree.workspace.Workspace;

/**
 * {@link TileStoreProvider} for {@link MergingTileStore}.
 *
 * @author <a href="mailto:Reijer.Copier@idgis.nl">Reijer Copier</a>
 * @author <a href="mailto:schneider@occamlabs.de">Markus Schneider</a>
 * @since 3.4
 */
public class MergingTileStoreProvider extends TileStoreProvider {

	private static final String CONFIG_NAMESPACE = "http://www.deegree.org/datasource/tile/merge";

	private static final URL SCHEMA = MergingTileStoreProvider.class
		.getResource("/META-INF/schemas/datasource/tile/merge/merge.xsd");

	@Override
	public String getNamespace() {
		return CONFIG_NAMESPACE;
	}

	@Override
	public ResourceMetadata<TileStore> createFromLocation(Workspace workspace, ResourceLocation<TileStore> location) {
		return new MergingTileStoreMetadata(workspace, location, this);
	}

	@Override
	public URL getSchema() {
		return SCHEMA;
	}

}
