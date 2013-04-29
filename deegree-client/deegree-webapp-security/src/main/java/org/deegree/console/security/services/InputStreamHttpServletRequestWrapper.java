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

package org.deegree.console.security.services;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;

/**
 * 
 * A wrapper around a {@link HttpServletRequest} that allows multiple access to the request body
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 *
 */
public class InputStreamHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final String body;
    
    private static final Logger LOG = getLogger( InputStreamHttpServletRequestWrapper.class );

    InputStreamHttpServletRequestWrapper( HttpServletRequest request ) {
        super( request );

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();

            if ( inputStream != null ) {
                bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );

                char[] charBuffer = new char[128];
                int bytesRead = -1;

                while ( ( bytesRead = bufferedReader.read( charBuffer ) ) > 0 ) {
                    stringBuilder.append( charBuffer, 0, bytesRead );
                }
            } else {
                stringBuilder.append( "" );
            }
        } catch ( IOException ex ) {
            LOG.warn( "Error reading the request body." );
        } finally {
            if ( bufferedReader != null ) {
                try {
                    bufferedReader.close();
                } catch ( IOException ex ) {
                    LOG.warn( "Error closing bufferedReader." );
                }
            }
        }

        body = stringBuilder.toString();
    }

    @Override
    public ServletInputStream getInputStream()
                            throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( body.getBytes() );

        ServletInputStream inputStream = new ServletInputStream() {
            public int read()
                                    throws IOException {
                return byteArrayInputStream.read();
            }
        };

        return inputStream;
    }

}