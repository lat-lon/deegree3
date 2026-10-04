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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class ExternalResourceChecker {

	private static final Logger LOG = LoggerFactory.getLogger(ExternalResourceChecker.class);

	private static final boolean ALLOW_ANY = TunableParameter.get("deegree.external-resouce-check.allow-any", false);

	private static final boolean AUDIT_ONLY = TunableParameter.get("deegree.external-resouce-check.audit-only", false);

	private static final List<ExternalResourceValidator> validators;

	static {
		if (ALLOW_ANY) {
			LOG.warn("External resource check disabled by configuration, no validation will be performed");
			validators = Collections.emptyList();
		}
		else {
			validators = StreamSupport.stream(ServiceLoader.load(ExternalResourceValidator.class).spliterator(), false)
				.peek(validator -> {
					LOG.debug("Loading external resource validator {} ({})", validator.getClass().getName(),
							validator.getOrder());
					validator.init();
				})
				.sorted(Comparator.comparingInt(ExternalResourceValidator::getOrder))
				.toList();
			if (validators.isEmpty()) {
				LOG.info("No external resource validator found. Please check your configuration.");
			}
			if (AUDIT_ONLY) {
				LOG.warn("External resource validation is set to audit only, validation issue will be logged.");
			}
		}
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isAllowed(URL url) {
		if (ALLOW_ANY) {
			return true;
		}

		boolean result = false;
		for (ExternalResourceValidator validator : validators) {
			Boolean validatorResult = validator.isValid(url);
			if (validatorResult != null) {
				result = validatorResult;
				break;
			}
		}

		if (!result && AUDIT_ONLY) {
			LOG.warn("[AUDIT] External resource '{}' should have been blocked.", url);
			return true;
		}
		LOG.debug("External resource check '{}': {}", url, result);

		return result;
	}

}
