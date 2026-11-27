/*----------------------------------------------------------------------------
 This file is part of deegree
 Copyright (C) 2026 by:
 - Department of Geography, University of Bonn -
 and
 - grit graphische Informationstechnik Beratungsgesellschaft mbH -
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
package org.deegree.base;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExternalResourceCheckerTest {

	@Test
	public void testDefaultsToInvalid() throws MalformedURLException {
		assertTrue(ExternalResourceChecker.isAllowed(null));
		// noinspection HttpUrlsUsage
		assertFalse(ExternalResourceChecker.isAllowed(new URL("http://example.com/forbidden")));
		assertFalse(ExternalResourceChecker.isAllowed(new URL("https://example.com/forbidden")));
		assertFalse(ExternalResourceChecker.isAllowed(new URL("ftp://example.com/forbidden")));
	}

	/**
	 * Test for file:// allowed through configuration (for testing)
	 * <p>
	 * Note that {@link ExternalResourceValidatorAnyFile} was loaded in module
	 * <code>deegree-core-test-logging</code>
	 */
	@Test
	public void testAllowedFile() throws MalformedURLException {
		// NOTE: explicitly enabled for integration tests in
		// deegree-core-test-logging/src/main/resources/META-INF/services/org.deegree.base.ExternalResourceValidator
		assertTrue(ExternalResourceChecker.isAllowed(new URL("file://abc")));
	}

}
