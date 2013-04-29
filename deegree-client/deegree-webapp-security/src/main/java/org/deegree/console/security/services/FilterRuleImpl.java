package org.deegree.console.security.services;

import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * @author <a href="mailto:erben@lat-lon.de">Alexander Erben</a>
 *
 */
public class FilterRuleImpl implements FilterRule {

    private final String allowedRole;

    private final String matchMethod;

    public FilterRuleImpl( String matchMethod, String allowedRole ) {
        this.allowedRole = allowedRole;
        this.matchMethod = matchMethod;
    }

    @Override
    public boolean canHandle( String requestUrl, Map<String, String> paramMap ) {
        String operation = paramMap.get( "REQUEST" );
        return matchMethod.equalsIgnoreCase( operation );
    }

    @Override
    public boolean canHandle( String requestUrl, XMLStreamReader reader ) {
        String operation = reader.getName().getLocalPart().toString();
        return matchMethod.equalsIgnoreCase( operation );
    }

    @Override
    public boolean isPermitted( String requestUrl, Map<String, String> paramMap, Authentication authentication ) {
        if ( !canHandle( requestUrl, paramMap ) )
            return true;
        return checkPermission( authentication );
    }

    @Override
    public boolean isPermitted( String requestUrl, XMLStreamReader reader, Authentication authentication ) {
        if ( !canHandle( requestUrl, reader ) )
            return true;
        return checkPermission( authentication );
    }

    private boolean checkPermission( Authentication authentication ) {
        if ( "ROLE_ANONYMOUS".equalsIgnoreCase( allowedRole ) )
            return true;
        if (authentication == null)
            return false;
        for ( GrantedAuthority auth : authentication.getAuthorities() ) {
            if ( allowedRole.equals( auth.getAuthority() ) )
                return true;
        }
        return false;
    }
}
