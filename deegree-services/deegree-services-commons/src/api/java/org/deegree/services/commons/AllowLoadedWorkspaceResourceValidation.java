package org.deegree.services.commons;

import org.deegree.base.ExternalResourceValidator;
import org.deegree.commons.utils.TunableParameter;
import org.deegree.services.controller.OGCFrontController;
import org.deegree.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AllowLoadedWorkspaceResourceValidation implements ExternalResourceValidator {

	private static final Logger LOG = LoggerFactory.getLogger(AllowLoadedWorkspaceResourceValidation.class);

	private static final boolean ENABLED = TunableParameter
		.get("deegree.external-resouce-check.workspace-loaded.enabled", true);

	public AllowLoadedWorkspaceResourceValidation() {
		if (!ENABLED) {
			LOG.warn("Disabled by configuration.");
		}
	}

	@Override
	public int getOrder() {
		return -100;
	}

	@Override
	public Boolean isValid(URL url) {
		if (url == null || !ENABLED || !"file".equalsIgnoreCase(url.getProtocol())) {
			return null;
		}
		Path file;
		Path workspaceLocation;
		try {
			file = Paths.get(url.toURI()).toAbsolutePath().normalize();
			workspaceLocation = OGCFrontController.getServiceWorkspace().getLocation().toPath();
		}
		catch (RuntimeException re) {
			LOG.debug("No workspace loaded, no exception regarding allowed folders apply");
			return null;
		}
		catch (URISyntaxException ex) {
			LOG.debug("Broken file URL ignored, was: {}", ex.getMessage());
			LOG.trace("Exception", ex);
			return null;
		}

		if (file.startsWith(workspaceLocation)) {
			// file is a child of the workspace
			return true;
		}

		return null;
	}

}
