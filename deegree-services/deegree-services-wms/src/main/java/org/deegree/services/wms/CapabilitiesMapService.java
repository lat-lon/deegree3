package org.deegree.services.wms;

import java.util.List;

import org.deegree.theme.Theme;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public interface CapabilitiesMapService {

	int getCurrentUpdateSequence();

	List<Theme> getThemes();

}
