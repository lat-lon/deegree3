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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;

import org.apache.commons.io.IOUtils;
import org.deegree.console.security.services.FilterRule;
import org.deegree.console.security.services.FilterRuleImpl;
import org.deegree.console.security.services.OGCServiceFilter;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml", "/testSecurityLayer.xml" })
public class OGCServiceFilterTest {

    @Autowired
    private UserDetailsService userDetailsService;

    private static final String GETCAPABILITIES_OPERATION = "getcapabilities";

    private static final String GETRECORDS_OPERATION = "getrecords";

    private static final String GETRECORDBYID_OPERATION = "getrecordbyid";

    private static final String GETCAPABILITIES_ROLE = "ROLE_ANONYMOUS";

    private static final String GETRECORDS_ROLE = "ROLE_GETRECORDS";

    private static final String GETRECORDBYID_ROLE = "ROLE_GETRECORDBYID";

    private static final String GETRECORDS_USER = "getrecords";

    private static final String GETRECORDBYID_USER = "getrecordbyid";

    private static final String GETRECORDS_POSTBODY = convertXmlFileToString( "/getRecords_PostBody.xml" );

    private static final String GETRECORDS_POSTBODY_SOAP = convertXmlFileToString( "/getRecords_SoapBody.xml" );

    // The query strings are not complete, but sufficient to check the request parameter
    private static final String GETRECORDS_KVP = "version=2.0.2&REQUEST=GetRecords";

    private static final String GETCAPABILITIES_KVP = "REQUEST=getCapabilities";

    private static final String GETRECORDBYID_KVP = "REQUEST=getRecordById";

    private final FilterRuleImpl getRecordsFilterRule = new FilterRuleImpl( GETRECORDS_OPERATION, GETRECORDS_ROLE );

    private final FilterRuleImpl getRecordByIdFilterRule = new FilterRuleImpl( GETRECORDBYID_OPERATION,
                                                                               GETRECORDBYID_ROLE );

    private final FilterRuleImpl getCapabilitiesFilterRule = new FilterRuleImpl( GETCAPABILITIES_OPERATION,
                                                                                 GETCAPABILITIES_ROLE );

    @After
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGetKvp()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setQueryString( GETCAPABILITIES_KVP );
        req.setMethod( "GET" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );

        req.setQueryString( GETRECORDS_KVP );
        req.setMethod( "GET" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDS_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );

        req.setQueryString( GETRECORDBYID_KVP );
        req.setMethod( "GET" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDBYID_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetKvpNotPermitted()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setQueryString( GETRECORDBYID_KVP );
        req.setMethod( "GET" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test
    public void testPostXml()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_POSTBODY );
        req.setMethod( "POST" );
        req.setContentType( "application/xml" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDS_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test
    public void testPostXmlSoap()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_POSTBODY_SOAP );
        req.setMethod( "POST" );
        req.setContentType( "application/xml" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDS_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }
    
    @Test(expected = AccessDeniedException.class)
    public void testPostXmlSoapNotPermitted()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_POSTBODY_SOAP );
        req.setMethod( "POST" );
        req.setContentType( "application/xml" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test(expected = AccessDeniedException.class)
    public void testPostXmlNotPermitted()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_POSTBODY );
        req.setMethod( "POST" );
        req.setContentType( "application/xml" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test
    public void testPostKvp()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_KVP );
        req.setMethod( "POST" );
        req.setContentType( "application/x-www-form-urlencoded" );
        AuthenticationUtils.authenticate( userDetailsService, GETRECORDS_USER );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    @Test(expected = AccessDeniedException.class)
    public void testPostKvpNotPermitted()
                            throws IOException, ServletException {
        final OGCServiceFilter filter = getFilter();
        MockInputStreamServletRequest req = new MockInputStreamServletRequest( GETRECORDS_KVP );
        req.setMethod( "POST" );
        req.setContentType( "application/x-www-form-urlencoded" );
        filter.doFilter( req, new MockHttpServletResponse(), new MockFilterChain() );
    }

    private OGCServiceFilter getFilter() {
        List<FilterRule> rules = new ArrayList<FilterRule>();
        rules.add( getRecordsFilterRule );
        rules.add( getRecordByIdFilterRule );
        rules.add( getCapabilitiesFilterRule );
        final OGCServiceFilter filter = new OGCServiceFilter( rules, false );
        return filter;
    }

    private class MockInputStreamServletRequest extends MockHttpServletRequest {

        private String body;

        public MockInputStreamServletRequest( String body ) {
            this.body = body;
        }

        @Override
        public ServletInputStream getInputStream() {
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

    private static String convertXmlFileToString( String inputXmlFile ) {
        try {
            InputStream inputStream = FilterRuleImplTest.class.getResourceAsStream( inputXmlFile );
            StringWriter stringWriter = new StringWriter();
            IOUtils.copy( inputStream, stringWriter, "UTF-8" );
            return stringWriter.toString();
        } catch ( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

}
