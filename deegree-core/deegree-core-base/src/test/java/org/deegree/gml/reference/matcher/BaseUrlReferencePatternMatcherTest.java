package org.deegree.gml.reference.matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class BaseUrlReferencePatternMatcherTest {

	private BaseUrlReferencePatternMatcher matcher = new BaseUrlReferencePatternMatcher(
			"http://deegree.org/documentation");

	@Test
	public void test_constructor_null() {
		assertThrows(IllegalArgumentException.class, () -> {
			new BaseUrlReferencePatternMatcher(null);
		});
	}

	@Test
	public void test_constructor_empty() {
		assertThrows(IllegalArgumentException.class, () -> {

			new BaseUrlReferencePatternMatcher("");
		});
	}

	@Test
	public void testIsMatching() throws Exception {
		assertTrue(matcher.isMatching("http://deegree.org/documentation"));
		assertTrue(matcher.isMatching("http://deegree.org/documentation/webservices"));

		assertFalse(matcher.isMatching("https://deegree.org/documentation"));
		assertFalse(matcher.isMatching("http://www.deegree.org/documentation"));
		assertFalse(matcher.isMatching("http://deegree2.org/documentation"));

		assertFalse(matcher.isMatching(null));
	}

}