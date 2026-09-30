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

import org.deegree.base.ExternalResourceValidator;

import java.net.URL;

import static org.deegree.services.controller.OGCFrontController.getInstance;

public class OGCFrontControllerResourceValidator implements ExternalResourceValidator {

    @Override
    public Boolean isValid(URL url) {
        OGCFrontControllerResourceChecks checks = getInstance().getResourceChecks();
        if (checks == null) {
            return false;
        }
        if ("https".equalsIgnoreCase(url.getProtocol()) || "http".equalsIgnoreCase(url.getProtocol())) {
            return checks.isValidHost(url.getHost());
        }
        if ("file".equalsIgnoreCase(url.getProtocol())) {
            // no explicit checking here, so it is advised to run with limited file system permissions
            return true;
        }

        // do not allow any other protocol
        return false;
    }

}
