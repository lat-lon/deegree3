package org.deegree.services.wms.controller.capabilities;

import java.util.Map;

import org.deegree.commons.ows.metadata.CapabilitiesServiceIdentification;
import org.deegree.commons.utils.Pair;
import org.deegree.services.wms.CapabilitiesMapService;
import org.deegree.workspace.Initializable;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 * @since 8.1
 */
public interface WmsCapabilitiesManipulator extends Initializable {

	Pair<CapabilitiesMapService, CapabilitiesServiceIdentification> manipulateServiceIdentification(
			Map<String, String> customParameters, String getUrl, CapabilitiesMapService service,
			CapabilitiesServiceIdentification serviceIdentification);

}
