/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2013 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/

package org.deegree.console.security;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {

    public static Authentication authenticate( UserDetailsService service, String userName ) {

        UserDetails userDetails = service.loadUserByUsername( userName );

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken( userDetails,
                                                                                             userDetails.getPassword() );
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                                                                                              userDetails,
                                                                                              token.getCredentials(),
                                                                                              userDetails.getAuthorities() );
        result.setDetails( token.getDetails() );
        Authentication auth = result;
        getContext().setAuthentication( auth );
        auth = getContext().getAuthentication();
        return auth;
    }

}