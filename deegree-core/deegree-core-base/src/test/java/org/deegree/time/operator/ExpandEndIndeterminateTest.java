package org.deegree.time.operator;

import org.deegree.commons.tom.gml.property.Property;
import org.deegree.time.position.TimePosition;
import org.deegree.time.primitive.GenericTimeInstant;
import org.deegree.time.primitive.GenericTimePeriod;
import org.deegree.time.primitive.RelatedTime;
import org.deegree.time.primitive.TimeGeometricPrimitive;
import org.deegree.time.primitive.TimeInstant;
import org.deegree.time.primitive.TimePeriod;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.deegree.time.position.IndeterminateValue.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExpandEndIndeterminateTest {

	private final ExpandEndIndeterminate ad = new ExpandEndIndeterminate();

	@Test
	public void expandEndIndeterminateInstant() {
		assertExpandEndIndeterminate(period("00:00:02", null), instant("00:00:02"));
	}

	@Test
	public void expandEndIndeterminatePeriodNormal() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			ad.evaluate(period("00:00:02", "00:00:03"));
		});
		assertEquals(exception.getMessage(),
				"ExpandEndIndeterminate requires a time instant or a time period with indeterminate end");
	}

	@Test
	public void expandEndIndeterminatePeriodIndeterminateBegin() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			ad.evaluate(period(null, "00:00:03"));
		});
		assertEquals(exception.getMessage(),
				"ExpandEndIndeterminate requires a time instant or a time period with indeterminate end");
	}

	@Test
	public void expandEndIndeterminatePeriodIndeterminateEnd() {
		assertExpandEndIndeterminate(period("00:00:02", null), period("00:00:02", null));
	}

	@Test
	public void expandEndIndeterminatePeriodIndeterminateBoth() {
		assertExpandEndIndeterminate(period(null, null), period(null, null));
	}

	private TimeInstant instant(final String s) {
		final List<Property> props = emptyList();
		final List<RelatedTime> relatedTimes = emptyList();
		TimePosition pos = null;
		if (s == null) {
			pos = new TimePosition(null, null, UNKNOWN, "");
		}
		else {
			pos = new TimePosition(null, null, null, s);
		}
		return new GenericTimeInstant(null, props, relatedTimes, null, pos);
	}

	private TimePeriod period(final String t1, final String t2) {
		final TimeInstant begin = instant(t1);
		final TimeInstant end = instant(t2);
		final List<Property> props = emptyList();
		final List<RelatedTime> relatedTimes = emptyList();
		return new GenericTimePeriod(null, props, relatedTimes, null, begin, end);
	}

	private void assertExpandEndIndeterminate(final TimeGeometricPrimitive expected, final TimeGeometricPrimitive a) {
		assertTrue(new Equals().evaluate(expected, ad.evaluate(a)));
	}

}
