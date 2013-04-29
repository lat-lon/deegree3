package org.deegree.console.security.services;

import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.springframework.security.core.Authentication;

/**
 * 
 * Interface for filter rules to be applied in the {@link OGCServiceFilter}
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 *
 */
public interface FilterRule {

    /**
     * Checks if the given parameter {@link Map} of the request can be evaluated by this filter rule
     * @param requestUrl
     * @param paramMap
     * @return
     */
    boolean canHandle( String requestUrl, Map<String, String> paramMap );

    /**
     * Checks if the given user {@link Authentication} is permitted to access the request with the given parameter {@link Map}
     * 
     * @param requestUrl
     * @param paramMap
     * @param authentication
     * @return
     */
    boolean isPermitted( String requestUrl, Map<String, String> paramMap, Authentication authentication );

    /**
     * Checks if the given body as {@link XMLStreamReader} of the request can be evaluated by this filter rule
     * @param requestUrl
     * @param reader
     * @return
     */
    boolean canHandle( String requestUrl, XMLStreamReader reader );

    /**
     * Checks if the given user {@link Authentication} is permitted to access the request with the given given body as {@link XMLStreamReader}
     * 
     * @param requestUrl
     * @param paramMap
     * @param authentication
     * @return
     */
    boolean isPermitted( String requestUrl, XMLStreamReader reader, Authentication authentication );

}