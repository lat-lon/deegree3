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

import static org.deegree.console.security.AuthenticationUtils.authenticate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.deegree.commons.utils.kvp.KVPUtils;
import org.deegree.commons.xml.stax.XMLInputFactoryUtils;
import org.deegree.commons.xml.stax.XMLStreamUtils;
import org.deegree.console.security.services.FilterRuleImpl;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext.xml", "/testSecurityLayer.xml" })
public class FilterRuleImplTest {

    @Autowired
    private UserDetailsService userDetailsService;

    private static final String GETCAPABILITIES_OPERATION = "getcapabilities";

    private static final String GETRECORDS_OPERATION = "getrecords";

    private static final String GETRECORDBYID_OPERATION = "getrecordbyid";
    
    private static final String GETRECORDS_USER = "getrecords";

    private static final String GETRECORDBYID_USER = "getrecordbyid";

    private static final String GETCAPABILITIES_ROLE = "ROLE_ANONYMOUS";

    private static final String GETRECORDS_ROLE = "ROLE_GETRECORDS";

    private static final String GETRECORDBYID_ROLE = "ROLE_GETRECORDBYID";

    private static final String GETCAPABILITIES_POSTBODY = convertXmlFileToString( "/getCapabilities_PostBody.xml" );

    private static final String GETRECORDS_POSTBODY = convertXmlFileToString( "/getRecords_PostBody.xml" );

    private static final String GETRECORDBYID_POSTBODY = convertXmlFileToString( "/getRecordById_PostBody.xml" );
    
    private static final String GETRECORDS_POSTBODY_SOAP = convertXmlFileToString( "/getRecords_SoapBody.xml" );

    // The query strings are not complete, but sufficient to check the request parameter
    private static final String GETCAPABILITIES_POSTBODY_KVP = "version=2.0.2&REQUEST=GetCapabilities";

    private static final String GETRECORDS_POSTBODY_KVP = "version=2.0.2&REQUEST=GetRecords";

    private static final String GETRECORDBYID_POSTBODY_KVP = "version=2.0.2&REQUEST=GetRecordById";

    private final FilterRuleImpl getRecordsFilterRule = new FilterRuleImpl( GETRECORDS_OPERATION, GETRECORDS_ROLE );

    private final FilterRuleImpl getRecordByIdFilterRule = new FilterRuleImpl( GETRECORDBYID_OPERATION,
                                                                               GETRECORDBYID_ROLE );

    private final FilterRuleImpl getCapabilitiesFilterRule = new FilterRuleImpl( GETCAPABILITIES_OPERATION,
                                                                                 GETCAPABILITIES_ROLE );

    private final Map<String, String> getCapabilitiesMap = getPostKvpMap( GETCAPABILITIES_POSTBODY_KVP );

    private final Map<String, String> getRecordsMap = getPostKvpMap( GETRECORDS_POSTBODY_KVP );

    private final Map<String, String> getRecordByIdMap = getPostKvpMap( GETRECORDBYID_POSTBODY_KVP );

    private final Map<String, String> getRecordsParamMap = getKvpMap( GETRECORDS_OPERATION );

    private final Map<String, String> getCapabilitiesParamMap = getKvpMap( GETCAPABILITIES_OPERATION );

    private final Map<String, String> getRecordByIdParamMap = getKvpMap( GETRECORDBYID_OPERATION );

    @After
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testCanHandle() throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";

        XMLStreamReader getCapabilitiesStream = getPostBody( GETCAPABILITIES_POSTBODY );
        XMLStreamReader getRecordsStream = getPostBody( GETRECORDS_POSTBODY );
        XMLStreamReader getRecordByIdStream = getPostBody( GETRECORDBYID_POSTBODY );
        
        XMLStreamReader getRecordsStreamSoap = getPostBodySoap( GETRECORDS_POSTBODY_SOAP );

        assertTrue( getRecordsFilterRule.canHandle( requestUrl, getRecordsParamMap ) );
        assertTrue( getCapabilitiesFilterRule.canHandle( requestUrl, getCapabilitiesParamMap ) );
        assertTrue( getRecordByIdFilterRule.canHandle( requestUrl, getRecordByIdParamMap ) );

        assertTrue( getRecordsFilterRule.canHandle( requestUrl, getRecordsStream ) );
        assertTrue( getCapabilitiesFilterRule.canHandle( requestUrl, getCapabilitiesStream ) );
        assertTrue( getRecordByIdFilterRule.canHandle( requestUrl, getRecordByIdStream ) );
        
        assertTrue( getRecordsFilterRule.canHandle( requestUrl, getRecordsStreamSoap ) );

    }

    @Test
    public void testCanHandleFalseOperation() throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";
        XMLStreamReader getRecordsStream = getPostBody( GETRECORDS_POSTBODY );
        XMLStreamReader getRecordsStreamSoap = getPostBodySoap( GETRECORDS_POSTBODY_SOAP );

        assertFalse( getRecordsFilterRule.canHandle( requestUrl, getCapabilitiesParamMap ) );
        // The filter rule should return true for isPermitted if it cannot handle the request
        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, getCapabilitiesParamMap, null ) );
        
        assertFalse( getCapabilitiesFilterRule.canHandle( requestUrl, getRecordsStream ) );
        assertFalse( getCapabilitiesFilterRule.canHandle( requestUrl, getRecordsStreamSoap ) );

    }

    @Test
    public void testIsPermittedKVP() {
        String requestUrl = "http://foo.bar/services/csw";

        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdParamMap,
                                                         getContext().getAuthentication() ) );
    }

    @Test
    public void testIsNotPermittedKVP() {
        String requestUrl = "http://foo.bar/services/csw";

        assertFalse( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdParamMap,
                                                          getContext().getAuthentication() ) );
        authenticate( userDetailsService, GETRECORDS_USER );
        assertFalse( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdParamMap,
                                                          getContext().getAuthentication() ) );
    }

    @Test
    public void testIsPermittedPostXml()
                            throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";
        XMLStreamReader getCapabilitiesStream = getPostBody( GETCAPABILITIES_POSTBODY );
        XMLStreamReader getRecordsStream = getPostBody( GETRECORDS_POSTBODY );
        XMLStreamReader getRecordByIdStream = getPostBody( GETRECORDBYID_POSTBODY );
        
        assertTrue( getCapabilitiesFilterRule.isPermitted( requestUrl, getCapabilitiesStream,
                                                           getContext().getAuthentication() ) );
        authenticate( userDetailsService, GETRECORDS_USER );

        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, getRecordsStream, getContext().getAuthentication() ) );

        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdStream,
                                                         getContext().getAuthentication() ) );
    }
    
    @Test
    public void testIsPermittedPostSoap()
                            throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";
        authenticate( userDetailsService, GETRECORDS_USER );
        XMLStreamReader getRecordsStream = getPostBodySoap( GETRECORDS_POSTBODY_SOAP );
        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, getRecordsStream, getContext().getAuthentication() ) );
    }

    @Test
    public void testIsNotPermittedPostSoap()
                            throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";
        XMLStreamReader getRecordsStream = getPostBodySoap( GETRECORDS_POSTBODY_SOAP );
        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, getRecordsStream, getContext().getAuthentication() ) );
    }

    @Test
    public void testIsNotPermittedPostXml()
                            throws XMLStreamException {
        String requestUrl = "http://foo.bar/services/csw";

        XMLStreamReader getRecordsStream = getPostBody( GETRECORDS_POSTBODY );
        XMLStreamReader getRecordByIdStream = getPostBody( GETRECORDS_POSTBODY );

        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, getRecordsStream, getContext().getAuthentication() ) );

        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, getRecordByIdStream,
                                                       getContext().getAuthentication() ) );
    }
    
    
    @Test
    public void testIsPermittedPostKvp()
                            throws XMLStreamException, UnsupportedEncodingException {
        String requestUrl = "http://foo.bar/services/csw";

        assertTrue( getCapabilitiesFilterRule.isPermitted( requestUrl, getCapabilitiesMap,
                                                           getContext().getAuthentication() ) );

        authenticate( userDetailsService, GETRECORDS_USER );
        assertTrue( getRecordsFilterRule.isPermitted( requestUrl, getRecordsMap, getContext().getAuthentication() ) );

        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertTrue( getRecordByIdFilterRule.isPermitted( requestUrl, getRecordByIdMap, getContext().getAuthentication() ) );
    }

    @Test
    public void testIsPermittedPostKvpNotPermitted()
                            throws XMLStreamException, UnsupportedEncodingException {
        String requestUrl = "http://foo.bar/services/csw";

        authenticate( userDetailsService, GETRECORDBYID_USER );
        assertFalse( getRecordsFilterRule.isPermitted( requestUrl, getRecordsMap, getContext().getAuthentication() ) );
    }

    private Map<String, String> getPostKvpMap( String body ) {
        try {
            return KVPUtils.getNormalizedKVPMap( body, "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
            return null;
        }
    }

    private XMLStreamReader getPostBody( String body )
                            throws XMLStreamException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( body.getBytes() );
        XMLStreamReader xmlStream = XMLInputFactoryUtils.newSafeInstance().createXMLStreamReader( "...",
                                                                                                  byteArrayInputStream );
        // Jump to first element
        XMLStreamUtils.nextElement( xmlStream );
        return xmlStream;
    }
    

    
    private XMLStreamReader getPostBodySoap( String body ) throws XMLStreamException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( body.getBytes() );
        XMLStreamReader xmlStream = XMLInputFactoryUtils.newSafeInstance().createXMLStreamReader( "...",
                                                                                                  byteArrayInputStream );
        // Jump to first element of body
        XMLStreamUtils.nextElement( xmlStream );
        XMLStreamUtils.nextElement( xmlStream );
        XMLStreamUtils.nextElement( xmlStream );

        return xmlStream;
    }
    

    private Map<String, String> getKvpMap( String operation ) {
        Map<String, String> getRecordsParamMap = new HashMap<String, String>();
        getRecordsParamMap.put( "REQUEST", operation );
        return getRecordsParamMap;
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
