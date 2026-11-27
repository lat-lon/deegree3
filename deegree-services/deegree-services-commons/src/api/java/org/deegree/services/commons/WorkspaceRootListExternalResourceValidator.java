/*----------------------------------------------------------------------------
 This file is part of deegree
 Copyright (C) 2001-2026 by:
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

 website: https://www.deegree.org/
----------------------------------------------------------------------------*/
package org.deegree.services.commons;

import org.deegree.base.ExternalResourceValidator;
import org.deegree.commons.utils.TunableParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.deegree.commons.config.DeegreeWorkspace.getWorkspaceRoot;

public class WorkspaceRootListExternalResourceValidator implements ExternalResourceValidator {

	private static final Logger LOG = LoggerFactory.getLogger(WorkspaceRootListExternalResourceValidator.class);

    private static final boolean ENABLED = TunableParameter
            .get("deegree.external-resouce-check.workspace-list.enabled", true);

	private static final String OVERRIDE_LOCATION = TunableParameter
		.get("deegree.external-resouce-check.workspace-list.override-location", (String) null);

	private static final String LIST_NAME = "allowed-resource.list";

	private List<Check> checks = Collections.emptyList();

	public WorkspaceRootListExternalResourceValidator() {
		if(!ENABLED) {
            LOG.warn("Disabled by configuration.");
            return;
        }

        String workspaceRoot = OVERRIDE_LOCATION;

		if (workspaceRoot == null) {
			workspaceRoot = getWorkspaceRoot();
		}

		Path listFile = Path.of(workspaceRoot, LIST_NAME);

		if (!Files.isReadable(listFile)) {
			LOG.info("No readable allow/disallow list found in the workspace root at {}", listFile);
			return;
		}
		LOG.debug("Loading allow/disallow list from workspace root at {}", listFile);

		List<String> lines;
		try {
			lines = Files.readAllLines(listFile);
		}
		catch (IOException ex) {
			LOG.error("Loading allow/disallow list found in the workspace root at {} failed with: {}", listFile,
					ex.getMessage());
			LOG.trace("Exception", ex);
			return;
		}

		checks = lines.stream()
			.filter(Objects::nonNull)
			.filter(line -> !line.startsWith("#"))
			.filter(line -> !line.trim().isEmpty())
			.map(String::trim)
			.map(this::loadRule)
			.filter(Objects::nonNull)
			.toList();

		LOG.info("Loaded {} entries for the allow/disallo list found in the workspace root at {}", checks.size(),
				listFile);
	}

	private Check loadRule(String configurationLine) {
		String pattern = configurationLine;
		boolean negate = false;
		if (configurationLine.startsWith("!")) {
			pattern = configurationLine.substring(1);
			negate = true;
		}
		try {
			return new Check(negate, Pattern.compile(pattern));
		}
		catch (PatternSyntaxException pse) {
			LOG.warn("Ignoring pattern '{}', as it could not be parsed as a regular expression. Erorr was: {}",
					pse.getMessage());
			LOG.trace("Exception", pse);
		}
		return null;
	}

	@Override
	public Boolean isValid(URL url) {
		if (url == null || !ENABLED) {
			return null;
		}
		try {
			String textUrl = sanitize(url);
			return checks.stream() //
				.map(check -> check.check(textUrl))
				.findFirst()
				.orElse(null);
		}
		catch (Exception ex) {
			LOG.warn("Validation of resource {} failed with {}", url, ex.getMessage());
		}
		return null;

	}

	private String sanitize(URL url) throws URISyntaxException {
		if ("file".equalsIgnoreCase(url.getProtocol())) {
			Paths.get(url.toURI()).toAbsolutePath().normalize().toString();
		}
		return url.toExternalForm();
	}

	private static class Check {

		private boolean negated;

		private Pattern pattern;

		Check(boolean negate, Pattern pattern) {
			this.negated = negate;
			this.pattern = pattern;
		}

		boolean check(CharSequence input) {
			boolean res = pattern.matcher(input).matches();
			return negated ? !res : res;
		}

	}

}
