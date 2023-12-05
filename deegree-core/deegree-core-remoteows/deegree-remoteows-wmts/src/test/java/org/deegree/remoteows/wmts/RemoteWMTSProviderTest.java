/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
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
package org.deegree.remoteows.wmts;

import org.deegree.remoteows.RemoteOWSProvider;
import org.deegree.workspace.ResourceInitException;
import org.deegree.workspace.WorkspaceUtils;
import org.deegree.workspace.standard.DefaultWorkspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link RemoteWMTSProvider}.
 *
 * @author <a href="mailto:schneider@occamlabs.de">Markus Schneider</a>
 */
public class RemoteWMTSProviderTest {

	private DefaultWorkspace workspace;

	@BeforeEach
	public void setup() throws IOException {
		File f = File.createTempFile("workspace", "test");
		f.delete();
		workspace = new DefaultWorkspace(f);
		workspace.initAll();
	}

	@AfterEach
	public void destroy() {
		workspace.destroy();
	}

	@Test
	public void testCreateFromLocalCapabilitiesUrl() throws ResourceInitException, IOException, URISyntaxException {
		URL configUrl = RemoteWMTSProviderTest.class.getResource("example.xml");
		File file = new File(configUrl.toURI());
		RemoteWMTS wmts = (RemoteWMTS) WorkspaceUtils.activateFromFile(workspace, RemoteOWSProvider.class, "example",
				file);
		assertNotNull(wmts);
	}

	@Test
	public void testCreateFromInvalidConfig() throws ResourceInitException, URISyntaxException {
		URL configUrl = RemoteWMTSProviderTest.class.getResource("example.invalid");
		File file = new File(configUrl.toURI());
		assertThrows(Exception.class, () -> {
			WorkspaceUtils.activateFromFile(workspace, RemoteOWSProvider.class, "example", file);
		});
	}

}
