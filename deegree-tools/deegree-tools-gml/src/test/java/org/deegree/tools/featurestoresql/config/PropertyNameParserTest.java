package org.deegree.tools.featurestoresql.config;

import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class PropertyNameParserTest {

	private PropertyNameParser propertyNameParser = new PropertyNameParser();

	@Test
	public void testParsePropertiesWithPrimitiveHref() throws URISyntaxException {
		URL resource = PropertyNameParserTest.class.getResource("/listOfPropertiesWithPrimitiveHref");
		List<QName> qNames = propertyNameParser.parsePropertiesWithPrimitiveHref(resource.toURI());

		assertThat(qNames.size(), is(3));
		assertThat(qNames, hasItem(new QName("http://inspire.ec.europa.eu/schemas/gn/4.0", "nativeness")));
		assertThat(qNames, hasItem(new QName("nativeness")));
		assertThat(qNames, hasItem(new QName("nativenessWithSpace")));
	}

}