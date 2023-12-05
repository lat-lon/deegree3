package org.deegree.services.wmts;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public abstract class AbstractWmtsSimilarityIT {

	protected String createRequest(String request) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://localhost:");
		sb.append(System.getProperty("portnumber", "8080"));
		sb.append("/deegree-wmts-tests/services");
		sb.append(request);
		return sb.toString();
	}

}
