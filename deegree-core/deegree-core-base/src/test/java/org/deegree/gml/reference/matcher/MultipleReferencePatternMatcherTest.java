package org.deegree.gml.reference.matcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class MultipleReferencePatternMatcherTest {

	@Test
	public void testIsMatching_oneTrue() {
		MultipleReferencePatternMatcher matcher = new MultipleReferencePatternMatcher();
		matcher.addMatcherToApply(mockMatcher(true));
		matcher.addMatcherToApply(mockMatcher(false));

		assertTrue(matcher.isMatching("test"));
	}

	@Test
	public void testIsMatching_allTrue() {
		MultipleReferencePatternMatcher matcher = new MultipleReferencePatternMatcher();
		matcher.addMatcherToApply(mockMatcher(true));
		matcher.addMatcherToApply(mockMatcher(true));

		assertTrue(matcher.isMatching("test"));
	}

	@Test
	public void testIsMatching_allFalse() {
		MultipleReferencePatternMatcher matcher = new MultipleReferencePatternMatcher();
		matcher.addMatcherToApply(mockMatcher(false));
		matcher.addMatcherToApply(mockMatcher(false));

		assertFalse(matcher.isMatching("test"));
	}

	@Test
	public void testIsMatching_noMatchers() {
		MultipleReferencePatternMatcher matcher = new MultipleReferencePatternMatcher();

		assertFalse(matcher.isMatching("test"));
	}

	private ReferencePatternMatcher mockMatcher(boolean isMatching) {
		ReferencePatternMatcher mock = mock(ReferencePatternMatcher.class);
		when(mock.isMatching(anyString())).thenReturn(isMatching);
		return mock;
	}

}