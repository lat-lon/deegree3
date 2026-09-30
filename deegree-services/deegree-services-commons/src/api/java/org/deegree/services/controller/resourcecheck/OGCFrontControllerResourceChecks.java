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
package org.deegree.services.controller.resourcecheck;

import org.deegree.services.jaxb.controller.ExternalResourceCheckType;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

public class OGCFrontControllerResourceChecks {

	private static final Logger LOG = getLogger(OGCFrontControllerResourceChecks.class);

	private boolean allowAny = false;

	private Set<String> allowedLcHostNames = new HashSet<>();

	private Set<String> allowedLcDomainNames = new HashSet<>();

	public void load(ExternalResourceCheckType config) {
		this.allowAny = false;
		this.allowedLcHostNames.clear();
		this.allowedLcDomainNames.clear();

		if (config == null) {
			return;
		}
		if (config.isAllowAny()) {
			this.allowAny = true;
			LOG.info("External resource checks are disabled!");
			return;
		}

		config.getAllowHttpHost()
			.stream()
			.filter(Objects::nonNull)
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.map(String::toLowerCase)
			.forEach(host -> {
				if (host.startsWith("*.")) {
					allowedLcDomainNames.add(host.substring(1));
				}
				else {
					allowedLcHostNames.add(host);
				}
			});
		LOG.info("Loaded {} unique allowed hosts and {} unique allowed domains.", allowedLcHostNames.size(),
				allowedLcDomainNames.size());
	}

	public boolean isValidHost(String host) {
		if (allowAny) {
			return true;
		}
		if (host == null || host.isBlank()) {
			return false;
		}
		if (allowedLcHostNames.isEmpty() && allowedLcHostNames.isEmpty()) {
			return false;
		}
		String lcHost = host.trim().toLowerCase();
		if (allowedLcHostNames.contains(lcHost)) {
			return true;
		}
		// check if hostname ends with any of the allowed domains
		return allowedLcDomainNames.stream().anyMatch(s -> lcHost.endsWith(s));
	}

}
