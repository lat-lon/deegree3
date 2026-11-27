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

import org.deegree.commons.utils.TunableParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Comparator;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class ExternalResourceChecker {

	private static final Logger LOG = LoggerFactory.getLogger(ExternalResourceChecker.class);

	private static final boolean LOGGING = TunableParameter.get("deegree.external-resouce-check.logging", false);

	private static final boolean ALLOW_ANY = TunableParameter.get("deegree.external-resouce-check.allow-any", false);

	private static final boolean ALLOW_HTTP = TunableParameter.get("deegree.external-resouce-check.allow-http", false);

	private static final boolean ALLOW_FILE = TunableParameter.get("deegree.external-resouce-check.allow-file", false);

	private static final ServiceLoader<ExternalResourceValidator> VALIDATORS = ServiceLoader
		.load(ExternalResourceValidator.class);

	public static boolean isAllowed(URL url) {
		if (url == null) {
			return true;
		}
		if (LOGGING) {
			LOG.info("Checking resource access to {}", url);
		}
		else {
			LOG.trace("Checking resource access to {}", url);
		}
		if (ALLOW_ANY) {
			return true;
        }
		else if (ALLOW_HTTP
				&& ("https".equalsIgnoreCase(url.getProtocol()) || "http".equalsIgnoreCase(url.getProtocol()))) {
			return true;
		}
		else if (ALLOW_FILE && "file".equalsIgnoreCase(url.getProtocol())) {
			return true;
		}

		return StreamSupport.stream(VALIDATORS.spliterator(), false)
			.sorted(Comparator.comparingInt(ExternalResourceValidator::getOrder))
			.map(val -> val.isValid(url))
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(Boolean.FALSE)
			.booleanValue();
	}

}
