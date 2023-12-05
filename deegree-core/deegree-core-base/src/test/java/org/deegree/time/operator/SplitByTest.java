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

public class SplitByTest {

	private final SplitBy splitBy = new SplitBy();

	@Test
	public void evaluateBegins() {
		assertSplitBy(null, instant("00:00:02"), null, instant("00:00:02"), period("00:00:02", "00:00:03"));
		assertSplitBy(null, instant("00:00:02"), null, instant("00:00:02"), period("00:00:02", null));
		assertSplitBy(null, period("00:00:02", "00:00:05"), null, period("00:00:02", "00:00:05"),
				period("00:00:02", "00:00:06"));
		assertSplitBy(null, period("00:00:02", "00:00:05"), null, period("00:00:02", "00:00:05"),
				period("00:00:02", null));
	}

	@Test
	public void evaluateBegunBy() {
		assertSplitBy(null, period("00:00:02", "00:00:03"), period("00:00:03", "00:00:05"),
				period("00:00:02", "00:00:05"), period("00:00:02", "00:00:03"));
		assertSplitBy(null, period("00:00:02", "00:00:03"), period("00:00:03", null), period("00:00:02", null),
				period("00:00:02", "00:00:03"));
	}

	@Test
	public void evaluateBegunByInstant1() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			splitBy.evaluate(period("00:00:02", "00:00:05"), instant("00:00:02"));
		});
		assertEquals("Invalid arguments for SplitBy", thrown.getMessage());
	}

	@Test
	public void evaluateBegunByInstant2() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			splitBy.evaluate(period("00:00:02", null), instant("00:00:02"));
		});
		assertEquals("Invalid arguments for SplitBy", thrown.getMessage());
	}

	@Test
	public void evaluateEnds() {
		assertSplitBy(null, period("00:00:02", "00:00:05"), null, period("00:00:02", "00:00:05"),
				period("00:00:01", "00:00:05"));
		assertSplitBy(null, period("00:00:02", "00:00:05"), null, period("00:00:02", "00:00:05"),
				period(null, "00:00:05"));
	}

	@Test
	public void evaluateEndedBy() {
		assertSplitBy(period("00:00:02", "00:00:04"), period("00:00:04", "00:00:05"), null,
				period("00:00:02", "00:00:05"), period("00:00:04", "00:00:05"));
		assertSplitBy(period(null, "00:00:04"), period("00:00:04", "00:00:05"), null, period(null, "00:00:05"),
				period("00:00:04", "00:00:05"));
	}

	@Test
	public void evaluateTContains() {
		assertSplitBy(period("00:00:02", "00:00:03"), period("00:00:03", "00:00:04"), period("00:00:04", "00:00:05"),
				period("00:00:02", "00:00:05"), period("00:00:03", "00:00:04"));
		assertSplitBy(period("00:00:02", "00:00:03"), period("00:00:03", "00:00:04"), period("00:00:04", null),
				period("00:00:02", null), period("00:00:03", "00:00:04"));
		assertSplitBy(period(null, "00:00:03"), period("00:00:03", "00:00:04"), period("00:00:04", "00:00:05"),
				period(null, "00:00:05"), period("00:00:03", "00:00:04"));
		assertSplitBy(period(null, "00:00:03"), period("00:00:03", "00:00:04"), period("00:00:04", null),
				period(null, null), period("00:00:03", "00:00:04"));
	}

	@Test
	public void evaluateTContainsInstant1() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			splitBy.evaluate(period("00:00:02", "00:00:05"), instant("00:00:03"));
		});
		assertEquals("Invalid arguments for SplitBy", thrown.getMessage());

	}

	@Test
	public void evaluateTContainsInstant2() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			splitBy.evaluate(period("00:00:02", null), instant("00:00:03"));
		});
		assertEquals("Invalid arguments for SplitBy", thrown.getMessage());

	}

	@Test
	public void evaluateTContainsInstant3() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			splitBy.evaluate(period(null, "00:00:05"), instant("00:00:03"));
		});
		assertEquals("Invalid arguments for SplitBy", thrown.getMessage());

	}

	@Test
	public void evaluateTContainsInstant4() {
		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			splitBy.evaluate(period(null, null), instant("00:00:03"));
		});
		assertEquals("Invalid arguments for SplitBy", thrown.getMessage());
	}

	@Test
	public void evaluateDuring() {
		assertSplitBy(null, instant("00:00:02"), null, instant("00:00:02"), period("00:00:01", "00:00:06"));
		assertSplitBy(null, instant("00:00:02"), null, instant("00:00:02"), period("00:00:01", null));
		assertSplitBy(null, instant("00:00:02"), null, instant("00:00:02"), period(null, "00:00:06"));
		assertSplitBy(null, instant("00:00:02"), null, instant("00:00:02"), period(null, null));
		assertSplitBy(null, period("00:00:02", "00:00:05"), null, period("00:00:02", "00:00:05"),
				period("00:00:01", "00:00:06"));
		assertSplitBy(null, period("00:00:02", "00:00:05"), null, period("00:00:02", "00:00:05"),
				period("00:00:01", null));
		assertSplitBy(null, period("00:00:02", "00:00:05"), null, period("00:00:02", "00:00:05"),
				period(null, "00:00:06"));
		assertSplitBy(null, period("00:00:02", "00:00:05"), null, period("00:00:02", "00:00:05"), period(null, null));
	}

	@Test
	public void evaluateTEquals() {
		assertSplitBy(null, instant("00:00:02"), null, instant("00:00:02"), instant("00:00:02"));
		assertSplitBy(null, period("00:00:02", "00:00:05"), null, period("00:00:02", "00:00:05"),
				period("00:00:02", "00:00:05"));
		assertSplitBy(null, period("00:00:02", null), null, period("00:00:02", null), period("00:00:02", null));
		assertSplitBy(null, period(null, "00:00:05"), null, period(null, "00:00:05"), period(null, "00:00:05"));
		assertSplitBy(null, period(null, null), null, period(null, null), period(null, null));
	}

	@Test
	public void evaluateTOverlaps() {
		assertSplitBy(period("00:00:02", "00:00:04"), period("00:00:04", "00:00:05"), null,
				period("00:00:02", "00:00:05"), period("00:00:04", "00:00:06"));
		assertSplitBy(period("00:00:02", "00:00:04"), period("00:00:04", "00:00:05"), null,
				period("00:00:02", "00:00:05"), period("00:00:04", null));
		assertSplitBy(period(null, "00:00:04"), period("00:00:04", "00:00:05"), null, period(null, "00:00:05"),
				period("00:00:04", "00:00:06"));
		assertSplitBy(period(null, "00:00:04"), period("00:00:04", "00:00:05"), null, period(null, "00:00:05"),
				period("00:00:04", null));
	}

	@Test
	public void evaluateOverlappedBy() {
		assertSplitBy(null, period("00:00:02", "00:00:03"), period("00:00:03", "00:00:05"),
				period("00:00:02", "00:00:05"), period("00:00:01", "00:00:03"));
		assertSplitBy(null, period("00:00:02", "00:00:03"), period("00:00:03", "00:00:05"),
				period("00:00:02", "00:00:05"), period(null, "00:00:03"));
		assertSplitBy(null, period("00:00:02", "00:00:03"), period("00:00:03", null), period("00:00:02", null),
				period("00:00:01", "00:00:03"));
		assertSplitBy(null, period("00:00:02", "00:00:03"), period("00:00:03", null), period("00:00:02", null),
				period(null, "00:00:03"));
	}

	@Test
	public void evaluateAfter() {
		assertSplitBy(instant("00:00:02"), null, null, instant("00:00:02"), instant("00:00:01"));
		assertSplitBy(instant("00:00:02"), null, null, instant("00:00:02"), period("00:00:00", "00:00:01"));
		assertSplitBy(instant("00:00:02"), null, null, instant("00:00:02"), period(null, "00:00:01"));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"), instant("00:00:01"));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"),
				period("00:00:00", "00:00:01"));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"),
				period(null, "00:00:01"));
		assertSplitBy(period("00:00:02", null), null, null, period("00:00:02", null), instant("00:00:01"));
		assertSplitBy(period("00:00:02", null), null, null, period("00:00:02", null), period("00:00:00", "00:00:01"));
		assertSplitBy(period("00:00:02", null), null, null, period("00:00:02", null), period(null, "00:00:01"));
	}

	@Test
	public void evaluateBefore() {
		assertSplitBy(instant("00:00:05"), null, null, instant("00:00:05"), instant("00:00:06"));
		assertSplitBy(instant("00:00:05"), null, null, instant("00:00:05"), period("00:00:06", "00:00:07"));
		assertSplitBy(instant("00:00:05"), null, null, instant("00:00:05"), period("00:00:06", null));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"), instant("00:00:06"));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"),
				period("00:00:06", "00:00:07"));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"),
				period("00:00:06", null));
		assertSplitBy(period(null, "00:00:05"), null, null, period(null, "00:00:05"), instant("00:00:06"));
		assertSplitBy(period(null, "00:00:05"), null, null, period(null, "00:00:05"), period("00:00:06", "00:00:07"));
		assertSplitBy(period(null, "00:00:05"), null, null, period(null, "00:00:05"), period("00:00:06", null));
	}

	@Test
	public void evaluateMeets() {
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"), instant("00:00:05"));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"),
				period("00:00:05", "00:00:06"));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"),
				period("00:00:05", null));
		assertSplitBy(period(null, "00:00:05"), null, null, period(null, "00:00:05"), instant("00:00:05"));
		assertSplitBy(period(null, "00:00:05"), null, null, period(null, "00:00:05"), period("00:00:05", "00:00:06"));
		assertSplitBy(period(null, "00:00:05"), null, null, period(null, "00:00:05"), period("00:00:05", null));
	}

	@Test
	public void evaluateMetBy() {
		assertSplitBy(instant("00:00:02"), null, null, instant("00:00:02"), period("00:00:01", "00:00:02"));
		assertSplitBy(instant("00:00:02"), null, null, instant("00:00:02"), period(null, "00:00:02"));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"),
				period("00:00:01", "00:00:02"));
		assertSplitBy(period("00:00:02", "00:00:05"), null, null, period("00:00:02", "00:00:05"),
				period(null, "00:00:02"));
		assertSplitBy(period("00:00:02", null), null, null, period("00:00:02", null), period("00:00:01", "00:00:02"));
		assertSplitBy(period("00:00:02", null), null, null, period("00:00:02", null), period(null, "00:00:02"));
	}

	private void assertSplitBy(final TimeGeometricPrimitive expectedBegin,
			final TimeGeometricPrimitive expectedIntersection, final TimeGeometricPrimitive expectedEnd,
			final TimeGeometricPrimitive a, final TimeGeometricPrimitive b) {
		final SplitByResult expected = new SplitByResult(expectedBegin, expectedIntersection, expectedEnd);
		final SplitByResult actual = splitBy.evaluate(a, b);
		assertTrue(expected.equals(actual));
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

}
