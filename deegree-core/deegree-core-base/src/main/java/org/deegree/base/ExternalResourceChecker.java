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
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

public class ExternalResourceChecker {

	private static final Logger LOG = LoggerFactory.getLogger(ExternalResourceChecker.class);

	private static boolean allowAny = TunableParameter.get("deegree.external-resouce-check.allow-any", false);

	private static boolean auditOnly = TunableParameter.get("deegree.external-resouce-check.audit-only", true);

	private static final ServiceLoader<ExternalResourceValidator> validatorLoader;

	static {
		validatorLoader = ServiceLoader.load(ExternalResourceValidator.class);
		for (ExternalResourceValidator validator : validatorLoader) {
			LOG.debug("Loaded {}", validator.getClass().getName());
			validator.init();
		}
	}

	public static boolean isAllowed(URL url) {
		if (allowAny) {
			return true;
		}

		boolean result = false;
		for (ExternalResourceValidator validator : validatorLoader) {
			Boolean validatorResult = validator.isValid(url);
			if (validatorResult != null) {
				result = validatorResult.booleanValue();
				break;
			}
		}

		if (!result && auditOnly) {
			LOG.warn("[AUDIT] External resource '{}' should have been blocked.", url);
			return true;
		}
		LOG.debug("External resource check '{}': {}", url, result);

		return result;
	}

}
