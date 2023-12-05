/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -
 and
 - Occam Labs UG (haftungsbeschränkt) -

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

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.workspace;

import org.deegree.db.ConnectionProvider;
import org.deegree.db.ConnectionProviderProvider;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.persistence.FeatureStoreProvider;
import org.deegree.workspace.graph.ResourceGraph;
import org.deegree.workspace.graph.ResourceNode;
import org.deegree.workspace.standard.DefaultResourceIdentifier;
import org.deegree.workspace.standard.DefaultWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for simplesql feature store dependency management.
 *
 * @author <a href="mailto:schmitz@occamlabs.de">Andreas Schmitz</a>
 */
public class SimpleSqlFeatureStoreTest {

	private DefaultWorkspace workspace;

	@BeforeEach
	public void setup() throws URISyntaxException {
		File dir = new File(SimpleSqlFeatureStoreTest.class.getResource("/workspace/").toURI());
		this.workspace = new DefaultWorkspace(dir);
		workspace.initAll();
	}

	@AfterEach
	public void shutdown() {
		workspace.destroy();
	}

	@Test
	public void testWorkspaceInitialized() {
		assertNotNull(workspace.getLocation(), "The feature store is expected to have a location.");
	}

	@Test
	public void testMissingDependency() {
		FeatureStore fs = workspace.getResource(FeatureStoreProvider.class, "simplesql-fail-missing-dep");
		assertNull(fs, "Feature store is expected not to be created.");
		ResourceMetadata<FeatureStore> md = workspace.getResourceMetadata(FeatureStoreProvider.class,
				"simplesql-fail-missing-dep");
		assertNotNull(md, "Resource metadata object is expected to be available.");
	}

	@Test
	public void testInvalidConfiguration() {
		FeatureStore fs = workspace.getResource(FeatureStoreProvider.class, "simplesql-fail-invalid-config");
		assertNull(fs, "Feature store is expected not to be created.");
		ResourceMetadata<FeatureStore> md = workspace.getResourceMetadata(FeatureStoreProvider.class,
				"simplesql-fail-invalid-config");
		assertNotNull(md, "Resource metadata object is expected to be available.");
	}

	@Test
	public void testValidConfiguration() {
		FeatureStore fs = workspace.getResource(FeatureStoreProvider.class, "simplesql-ok");
		assertNotNull(fs, "Feature store is expected to be created.");
		ResourceMetadata<FeatureStore> md = workspace.getResourceMetadata(FeatureStoreProvider.class, "simplesql-ok");
		assertNotNull(md, "Resource metadata object is expected to be available.");
		Set<ResourceIdentifier<? extends Resource>> deps = md.getDependencies();
		assertEquals(1, deps.size(), "Expected one dependency.");
		ResourceIdentifier<? extends Resource> id = deps.iterator().next();
		Resource r = workspace.getResource(id.getProvider(), id.getId());
		assertNotNull(r, "Expected dependency to be available.");
		assertInstanceOf(ConnectionProvider.class, r, "Expected dependency to be of type ConnectionProvider.");
	}

	@Test
	public void testResourceGraph() {
		ResourceGraph graph = workspace.getDependencyGraph();
		ResourceNode<FeatureStore> node = graph
			.getNode(new DefaultResourceIdentifier<FeatureStore>(FeatureStoreProvider.class, "simplesql-ok"));
		assertEquals(1, node.getDependencies().size(), "Expected one dependency.");
		ResourceNode<ConnectionProvider> node2 = graph.getNode(
				new DefaultResourceIdentifier<ConnectionProvider>(ConnectionProviderProvider.class, "simplesqlh2"));
		assertEquals(1, node2.getDependents().size(), "Expected one dependent.");
		node = graph.getNode(
				new DefaultResourceIdentifier<FeatureStore>(FeatureStoreProvider.class, "simplesql-fail-missing-dep"));
		assertFalse(node.areDependenciesAvailable(), "Expected broken dependencies.");
	}

	@Test
	@Disabled
	public void testDestroySingle() {
		FeatureStore fs = workspace.getResource(FeatureStoreProvider.class, "simplesql-ok");
		assertNotNull(fs, "Feature store is expected to be created.");
		workspace.destroy(
				new DefaultResourceIdentifier<ConnectionProvider>(ConnectionProviderProvider.class, "simplesqlh2"));
		fs = workspace.getResource(FeatureStoreProvider.class, "simplesql-ok");
		assertNull(fs, "Feature store is expected to be destroyed.");
		ConnectionProvider prov = workspace.getResource(ConnectionProviderProvider.class, "simplesqlh2");
		assertNull(prov, "Connection provider is expected to be destroyed.");
	}

	@Test
	@Disabled
	public void testDestroyInitializeSingle() {
		FeatureStore fs = workspace.getResource(FeatureStoreProvider.class, "simplesql-ok");
		assertNotNull(fs, "Feature store is expected to be created.");
		workspace.destroy(
				new DefaultResourceIdentifier<ConnectionProvider>(ConnectionProviderProvider.class, "simplesqlh2"));
		workspace.init(new DefaultResourceIdentifier<FeatureStore>(FeatureStoreProvider.class, "simplesql-ok"), null);
		fs = workspace.getResource(FeatureStoreProvider.class, "simplesql-ok");
		assertNotNull(fs, "Feature store is expected to be re-initialized.");
		ConnectionProvider prov = workspace.getResource(ConnectionProviderProvider.class, "simplesqlh2");
		assertNotNull(prov, "Connection provider is expected to be re-initialized.");
	}

	@Test
	@Disabled
	public void testReinitializeChain() {
		FeatureStore fs = workspace.getResource(FeatureStoreProvider.class, "simplesql-ok");
		assertNotNull(fs, "Feature store is expected to be created.");
		WorkspaceUtils.reinitializeChain(workspace,
				new DefaultResourceIdentifier<ConnectionProvider>(ConnectionProviderProvider.class, "simplesqlh2"));
		fs = workspace.getResource(FeatureStoreProvider.class, "simplesql-ok");
		assertNotNull(fs, "Feature store is expected to be re-initialized.");
		ConnectionProvider prov = workspace.getResource(ConnectionProviderProvider.class, "simplesqlh2");
		assertNotNull(prov, "Connection provider is expected to be re-initialized.");
	}

}
