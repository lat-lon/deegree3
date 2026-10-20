/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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

package org.deegree.rendering.r2d.strokes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import org.junit.Test;

public class OffsetStrokeTest {

	@Test
	public void preservesSignedOffsetAndOrdinaryCorners() {
		for (double offset : new double[] { -0.5, 0, 0.5 }) {
			Shape line = offset(path(0, 0, 10, 0), offset);
			assertEquals(offset, line.getBounds2D().getMinY(), 1E-12);
			assertEquals(10, line.getBounds2D().getWidth(), 1E-12);
			PathIterator corners = offset(path(0, 0, 10, 0, 10, 10), offset).getPathIterator(null);
			corners.next();
			double[] point = new double[6];
			assertEquals(PathIterator.SEG_LINETO, corners.currentSegment(point));
			assertEquals(10 - offset, point[0], 1E-12);
			assertEquals(offset, point[1], 1E-12);
		}
	}

	@Test
	public void boundsNearlyReversingPlanSegments() {
		// Original EPSG:25832 coordinates, one reversal from each affected feature.
		Path2D[] examples = { path(681136.5384, 5780833.1372, 681131.9228, 5780831.9292, 681132.2588, 5780832.0171),
				path(681308.9853, 5781019.9985, 681310.6655, 5781005.3101, 681310.0128, 5781011.0164),
				path(681127.9042, 5780907.4073, 681126.6695, 5780909.7513, 681126.7428, 5780909.6121),
				path(681326.5397, 5780998.5235, 681325.3926, 5781008.5512, 681325.3993, 5781008.4931),
				path(0, 0, 10, 0, 0, 0), path(0, 0, 10, 0, 0, 0.000001) };
		for (Path2D example : examples) {
			for (double distance : new double[] { -0.5, 0, 0.5 }) {
				assertBounded(example, offset(example, distance), Math.abs(distance));
			}
		}
	}

	@Test
	public void keepsSeparateSubpathsIndependent() {
		Path2D combined = path(0, 0, 10, 0);
		combined.append(path(20, 10, 10, 10.000001), false);
		for (double distance : new double[] { -0.5, 0.5 }) {
			Path2D expected = new Path2D.Double(offset(path(0, 0, 10, 0), distance));
			expected.append(offset(path(20, 10, 10, 10.000001), distance), false);
			assertSamePath(expected, offset(combined, distance));
		}
	}

	@Test
	public void handlesDuplicatePointsAndEmptyPaths() {
		assertSamePath(offset(path(0, 0, 10, 0), -0.5), offset(path(0, 0, 0, 0, 10, 0, 10, 0), -0.5));
		assertTrue(offset(new Path2D.Double(), -0.5).getPathIterator(null).isDone());
		assertTrue(offset(path(0, 0, 0, 0), -0.5).getPathIterator(null).isDone());
	}

	@Test
	public void preservesRingStartForGraphicStrokePhase() {
		Path2D ring = path(0, 0, 10, 0, 10, 10, 0, 10);
		ring.closePath();
		for (double distance : new double[] { -0.5, 0.5 }) {
			Path2D expected = path(0, distance, 10 - distance, distance, 10 - distance, 10 - distance, distance,
					10 - distance, distance, distance);
			expected.closePath();
			assertSamePath(expected, offset(ring, distance));
		}
	}

	@Test
	public void closesEachRingWithItsOwnCorner() {
		Path2D first = path(0, 0, 10, 0, 10, 10, 0, 10);
		first.closePath();
		Path2D second = path(20, 20, 30, 20, 30, 30, 20, 30, 20, 20);
		second.closePath();
		Path2D combined = new Path2D.Double(first);
		combined.append(second, false);
		Path2D expected = new Path2D.Double(offset(first, -0.5));
		expected.append(offset(second, -0.5), false);
		assertSamePath(expected, offset(combined, -0.5));
		assertBounded(first, offset(first, -0.5), 0.5);
	}

	@Test
	public void removesRetracesWithoutChangingEndpointsOrInput() {
		double[][] examples = {
				{ 681136.5384, 5780833.1372, 681131.9228, 5780831.9292, 681132.2588, 5780832.0171, 681123.9667,
						5780829.8469 },
				{ 681308.9853, 5781019.9985, 681310.6655, 5781005.3101, 681310.0128, 5781011.0164, 681312.5313,
						5780989.0003 },
				{ 681127.9042, 5780907.4073, 681126.6695, 5780909.7513, 681126.7428, 5780909.6121, 681102.0253,
						5780956.5373 },
				{ 681326.5397, 5780998.5235, 681325.3926, 5781008.5512, 681325.3993, 5781008.4931, 681323.9624,
						5781021.0542 },
				{ 681322.6999, 5781032.089, 681322.3441, 5781035.1994, 681322.3727, 5781034.9488, 681322.0585,
						5781037.6955 },
				{ 681267.2043, 5781073.9915, 681264.7922, 5781073.2861, 681264.8619, 5781073.3065, 681262.547,
						5781072.6295 } };
		for (double[] coordinates : examples) {
			Path2D original = path(coordinates);
			Path2D unchanged = new Path2D.Double(original);
			for (double distance : new double[] { -0.5, 0.5 }) {
				Shape expected = offset(path(coordinates[0], coordinates[1], coordinates[6], coordinates[7]), distance);
				assertSamePath(expected, offset(original, distance));
				assertSamePath(unchanged, original);
			}
		}
	}

	@Test
	public void retainsRealHairpinsAndExcursionsBeyondEndpoints() {
		Path2D[] examples = { path(0, 0, 10, 0, 5, 0.01, 20, 0.01), path(0, 0, 10, 0, 5, 0, 8, 0),
				path(0, 0, 10, 0, 0, 0) };
		for (Path2D example : examples) {
			int vertices = 0;
			PathIterator iterator = offset(example, -0.5).getPathIterator(null);
			while (!iterator.isDone()) {
				vertices++;
				iterator.next();
			}
			assertTrue("A real turn or excursion must not collapse to one segment", vertices > 2);
		}
	}

	private static Shape offset(Shape path, double distance) {
		return new OffsetStroke(distance, null, null).createStrokedShape(path);
	}

	private static Path2D path(double... coordinates) {
		Path2D result = new Path2D.Double();
		result.moveTo(coordinates[0], coordinates[1]);
		for (int i = 2; i < coordinates.length; i += 2) {
			result.lineTo(coordinates[i], coordinates[i + 1]);
		}
		return result;
	}

	private static void assertBounded(Shape original, Shape shifted, double margin) {
		Rectangle2D bounds = original.getBounds2D();
		PathIterator iterator = shifted.getPathIterator(null);
		double[] point = new double[6];
		while (!iterator.isDone()) {
			if (iterator.currentSegment(point) != PathIterator.SEG_CLOSE) {
				assertTrue(Double.isFinite(point[0]) && Double.isFinite(point[1]));
				assertTrue(point[0] >= bounds.getMinX() - margin - 1E-8);
				assertTrue(point[0] <= bounds.getMaxX() + margin + 1E-8);
				assertTrue(point[1] >= bounds.getMinY() - margin - 1E-8);
				assertTrue(point[1] <= bounds.getMaxY() + margin + 1E-8);
			}
			iterator.next();
		}
	}

	private static void assertSamePath(Shape expected, Shape actual) {
		PathIterator a = expected.getPathIterator(null), b = actual.getPathIterator(null);
		double[] pa = new double[6], pb = new double[6];
		while (!a.isDone()) {
			assertTrue(!b.isDone());
			assertEquals(a.currentSegment(pa), b.currentSegment(pb));
			assertEquals(pa[0], pb[0], 1E-10);
			assertEquals(pa[1], pb[1], 1E-10);
			a.next();
			b.next();
		}
		assertTrue(b.isDone());
	}

}
