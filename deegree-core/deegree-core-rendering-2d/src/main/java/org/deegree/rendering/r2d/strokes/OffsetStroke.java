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

import static java.awt.geom.PathIterator.SEG_CLOSE;
import static java.awt.geom.PathIterator.SEG_CUBICTO;
import static java.awt.geom.PathIterator.SEG_LINETO;
import static java.awt.geom.PathIterator.SEG_MOVETO;
import static java.awt.geom.PathIterator.SEG_QUADTO;
import static java.lang.Math.sqrt;
import static org.deegree.commons.utils.CollectionUtils.clearNulls;
import static org.deegree.commons.utils.math.MathUtils.isZero;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.deegree.commons.utils.Pair;
import org.deegree.style.styling.components.PerpendicularOffsetType;
import org.slf4j.Logger;

/**
 * <code>OffsetStroke</code>
 *
 * Idea: it would be good to combine the offset line and the line width line. In that case
 * the offset and line width could be adjusted dynamically when angles are too small.
 *
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 */
public class OffsetStroke implements Stroke {

	private static final Logger LOG = getLogger(OffsetStroke.class);

	private final double offset;

	private final Stroke stroke;

	private PerpendicularOffsetType type;

	/**
	 * @param offset
	 * @param stroke
	 * @param type may be null, default is Standard
	 */
	public OffsetStroke(double offset, Stroke stroke, PerpendicularOffsetType type) {
		if (type == null) {
			type = new PerpendicularOffsetType();
		}
		this.offset = offset;
		this.stroke = stroke;
		this.type = type;
	}

	private static double[] calcNormal(final double x1, final double y1, final double x2, final double y2,
			final double[] last) {
		final double nx = x2 - x1;
		final double ny = y2 - y1;
		if (isZero(nx) && isZero(ny)) {
			if (last != null) {
				LOG.debug("Two subsequent points in a curve have been the same. Using the last normal...");
				LOG.debug("Please implement proper generalization so this won't happen...");
				return last;
			}
			LOG.debug("Some of the first points in a curve were identical.");
			return null;
		}
		final double len = sqrt(nx * nx + ny * ny);
		return new double[] { -ny / len, nx / len };
	}

	private double[] calcIntersection(final double px, final double py, final double[] n1, final double[] n2) {
		double nx = px + offset * n1[0];
		double ny = py + offset * n1[1];
		if (n2 == null) {
			return new double[] { nx, ny };
		}

		// calc intersection point of the two lines that are parallel to the original
		// geometry lines
		double ox = px + offset * n2[0];
		double oy = py + offset * n2[1];
		double lam = (n1[1] * (oy - ny) + n1[0] * (ox - nx)) / (n2[0] * n1[1] - n1[0] * n2[1]);
		if (Double.isNaN(lam) || Double.isInfinite(lam)) {
			LOG.warn("Some geometries are broken (self-intersections, ...), rendering output may not look optimal.");
			return new double[] { nx, ny };
		}
		return new double[] { ox + lam * n2[1], oy - lam * n2[0] };
	}

	/** Maximum corner distance, measured in multiples of the offset magnitude. */
	private static final double OFFSET_MITER_LIMIT = 10;

	/**
	 * Offset linear subpaths independently. Return null for curved paths so that their
	 * existing handling is retained.
	 */
	private Shape createStandardLinearOffset(Shape original) {
		PathIterator iterator = original.getPathIterator(null);
		while (!iterator.isDone()) {
			int segment = iterator.currentSegment(new double[6]);
			if (segment == SEG_QUADTO || segment == SEG_CUBICTO) {
				return null;
			}
			iterator.next();
		}
		Path2D.Double result = new Path2D.Double(original.getPathIterator(null).getWindingRule());
		List<double[]> points = new ArrayList<double[]>();
		iterator = original.getPathIterator(null);
		while (!iterator.isDone()) {
			double[] coordinates = new double[6];
			switch (iterator.currentSegment(coordinates)) {
				case SEG_MOVETO:
					appendLinearOffset(result, points, false);
					points.clear();
					points.add(new double[] { coordinates[0], coordinates[1] });
					break;
				case SEG_LINETO:
					double[] previous = points.get(points.size() - 1);
					if (previous[0] != coordinates[0] || previous[1] != coordinates[1]) {
						points.add(new double[] { coordinates[0], coordinates[1] });
					}
					break;
				case SEG_CLOSE:
					appendLinearOffset(result, points, true);
					double[] start = points.isEmpty() ? null : points.get(0);
					points.clear();
					if (start != null) {
						points.add(start);
					}
					break;
			}
			iterator.next();
		}
		appendLinearOffset(result, points, false);
		return result;
	}

	private void appendLinearOffset(Path2D.Double result, List<double[]> points, boolean closed) {
		points = removeRetracedSegments(points);
		int count = points.size();
		if (count < 2) {
			return;
		}
		// Explicitly closed paths may also contain their first coordinate at the end.
		if (closed && points.get(0)[0] == points.get(count - 1)[0] && points.get(0)[1] == points.get(count - 1)[1]) {
			count--;
		}
		if (count < 2) {
			return;
		}
		double[][] normals = new double[closed ? count : count - 1][2];
		for (int i = 0; i < normals.length; i++) {
			double[] a = points.get(i);
			double[] b = points.get((i + 1) % count);
			double dx = b[0] - a[0], dy = b[1] - a[1];
			double length = Math.hypot(dx, dy);
			normals[i][0] = -dy / length;
			normals[i][1] = dx / length;
		}
		for (int i = 0; i < count; i++) {
			double[] point = points.get(i);
			// Keep the original start position along the first offset segment, including
			// rings: graphic strokes and dash patterns measure their phase from here.
			if (i == 0 || (!closed && i == count - 1)) {
				double[] normal = normals[i == 0 ? 0 : i - 1];
				appendOffsetPoint(result, point[0] + offset * normal[0], point[1] + offset * normal[1], i == 0);
			}
			else {
				appendOffsetJoin(result, point, normals[(i + normals.length - 1) % normals.length], normals[i], i == 0);
			}
		}
		if (closed) {
			appendOffsetJoin(result, points.get(0), normals[normals.length - 1], normals[0], false);
			result.closePath();
		}
	}

	/**
	 * Remove almost collinear out-and-back runs from the temporary offset input. Unlike
	 * general simplification this requires two reversals, preserves both endpoints and
	 * only removes vertices lying on the replacement segment within one thousandth of the
	 * offset magnitude.
	 */
	private List<double[]> removeRetracedSegments(List<double[]> points) {
		List<double[]> result = new ArrayList<double[]>();
		double tolerance = Math.abs(offset) * 0.001;
		for (double[] point : points) {
			result.add(point);
			while (result.size() >= 4) {
				int start = result.size() - 4;
				double[] a = result.get(start), b = result.get(start + 1);
				double[] c = result.get(start + 2), d = result.get(start + 3);
				double vx = c[0] - b[0], vy = c[1] - b[1];
				if ((b[0] - a[0]) * vx + (b[1] - a[1]) * vy >= 0 || vx * (d[0] - c[0]) + vy * (d[1] - c[1]) >= 0
						|| !isOnReplacementSegment(a, d, b, tolerance) || !isOnReplacementSegment(a, d, c, tolerance)) {
					break;
				}
				result.remove(start + 2);
				result.remove(start + 1);
			}
		}
		return result;
	}

	private static boolean isOnReplacementSegment(double[] a, double[] d, double[] point, double tolerance) {
		double dx = d[0] - a[0], dy = d[1] - a[1];
		double length = Math.hypot(dx, dy);
		if (length == 0) {
			return false;
		}
		dx /= length;
		dy /= length;
		double px = point[0] - a[0], py = point[1] - a[1];
		double along = px * dx + py * dy;
		return along >= 0 && along <= length && Math.abs(px * dy - py * dx) <= tolerance;
	}

	private void appendOffsetJoin(Path2D.Double result, double[] point, double[] before, double[] after, boolean move) {
		// Intersect equal-distance parallel lines in local coordinates, avoiding
		// subtraction of large absolute coordinates and an almost-zero determinant.
		double sx = before[0] + after[0], sy = before[1] + after[1];
		double sumLength = Math.hypot(sx, sy);
		if (sumLength >= 2 / OFFSET_MITER_LIMIT) {
			double factor = 2 * offset / (sumLength * sumLength);
			appendOffsetPoint(result, point[0] + factor * sx, point[1] + factor * sy, move);
		}
		else {
			// A bevel keeps both offset segment ends local even at a U-turn.
			appendOffsetPoint(result, point[0] + offset * before[0], point[1] + offset * before[1], move);
			appendOffsetPoint(result, point[0] + offset * after[0], point[1] + offset * after[1], false);
		}
	}

	private static void appendOffsetPoint(Path2D.Double result, double x, double y, boolean move) {
		if (move) {
			result.moveTo(x, y);
		}
		else {
			result.lineTo(x, y);
		}
	}

	public Shape createStrokedShape(final Shape p) {
		if (type.type == PerpendicularOffsetType.Type.Standard) {
			Shape linear = createStandardLinearOffset(p);
			if (linear != null) {
				return handleSubstraction(stroke == null ? linear : stroke.createStrokedShape(linear), p);
			}
		}

		LinkedList<Pair<Integer, double[]>> list = new LinkedList<Pair<Integer, double[]>>();
		LinkedList<double[]> normals = new LinkedList<double[]>();

		PathIterator i = p.getPathIterator(null);

		while (!i.isDone()) {
			double[] ps = new double[6];
			list.add(new Pair<Integer, double[]>(i.currentSegment(ps), ps));
			i.next();
		}

		double firstx = list.peek().second[0], firsty = list.peek().second[1];

		// calc normals
		double[] last = calculateNormals(normals, firstx, firsty, list);

		// calc new path
		// ATTENTION: at least for cubic to this does not work! VM crash...
		double[] firstNormal = normals.peek();
		if (last == null) {
			last = normals.peekLast();
		}

		boolean firstMove = true;

		Path2D.Double path = new Path2D.Double();
		for (Pair<Integer, double[]> pair : list) {
			switch (pair.first) {
				case SEG_CLOSE:
					handleClose(last, firstNormal, firstx, firsty, path);
					break;
				case SEG_CUBICTO:
					double[] n1 = normals.poll();
					double[] n2 = normals.poll();
					double[] n3 = normals.poll();
					n1 = calcIntersection(pair.second[0], pair.second[1], n1, n2);
					n2 = calcIntersection(pair.second[2], pair.second[3], n2, n3);
					n3 = calcIntersection(pair.second[4], pair.second[5], n3, normals.peek());
					path.curveTo(n1[0], n1[1], n2[0], n2[1], n3[0], n3[1]);
					break;
				case SEG_LINETO:
					handleLineTo(normals, pair, firstMove, path);
					continue;
				case SEG_MOVETO:
					n1 = normals.peek();
					firstMove = false;
					path.moveTo(pair.second[0] + n1[0] * offset, pair.second[1] + n1[1] * offset);
					break;
				case SEG_QUADTO:
					n1 = normals.poll();
					n2 = normals.poll();
					n1 = calcIntersection(pair.second[0], pair.second[1], n1, n2);
					n2 = calcIntersection(pair.second[2], pair.second[3], n2, normals.peek());
					path.quadTo(n1[0], n1[1], n2[0], n2[1]);
					break;
			}
		}

		Shape res = stroke == null ? path : stroke.createStrokedShape(path);

		res = handleSubstraction(res, p);

		return res;
	}

	private Shape handleSubstraction(Shape res, Shape p) {
		switch (type.substraction) {
			case None:
				break;
			case NegativeOffset:
				Shape substractMe = new OffsetStroke(-offset, null, null).createStrokedShape(p);
				substractMe = stroke == null ? substractMe : stroke.createStrokedShape(substractMe);
				Area a = new Area(res);
				a.subtract(new Area(substractMe));
				res = a;
				break;
		}
		return res;
	}

	private void handleLineTo(LinkedList<double[]> normals, Pair<Integer, double[]> pair, boolean firstMove,
			java.awt.geom.Path2D.Double path) {
		double[] n1 = normals.poll();
		double[] n2 = normals.peek();
		if (n1 == n2) {
			return;
		}
		switch (type.type) {
			case Edged:
				if (n2 == null) {
					n1 = calcIntersection(pair.second[0], pair.second[1], n1, null);
					maybeLineTo(path, n1[0], n1[1]);
					return;
				}
				double[] n = new double[] { n1[0] + n2[0], n1[1] + n2[1] };
				double len = sqrt(n[0] * n[0] + n[1] * n[1]);
				n[0] /= len;
				n[1] /= len;
				n1 = calcIntersection(pair.second[0], pair.second[1], n1, n);
				maybeLineTo(path, n1[0], n1[1]);
				n1 = calcIntersection(pair.second[0], pair.second[1], n, n2);
				maybeLineTo(path, n1[0], n1[1]);
				return;
			case Round:
				double[] p1 = new double[] { pair.second[0] + n1[0] * offset, pair.second[1] + n1[1] * offset };
				maybeLineTo(path, p1[0], p1[1]);
				if (n2 == null) {
					return;
				}
				double[] p2 = new double[] { pair.second[0] + n2[0] * offset, pair.second[1] + n2[1] * offset };
				double[] midp = new double[] { p1[0] + n2[0] * offset, p1[1] + n2[1] * offset };
				path.quadTo(midp[0], midp[1], p2[0], p2[1]);
				return;
			case Standard:
				n1 = calcIntersection(pair.second[0], pair.second[1], n1, n2);
				if (firstMove) {
					path.moveTo(n1[0], n1[1]);
					firstMove = false;
				}
				else {
					maybeLineTo(path, n1[0], n1[1]);
				}
				return;
		}
	}

	private void handleClose(double[] last, double[] firstNormal, double firstx, double firsty,
			java.awt.geom.Path2D.Double path) {
		switch (type.type) {
			case Edged:
				double[] n = new double[] { last[0] + firstNormal[0], last[1] + firstNormal[1] };
				double len = sqrt(n[0] * n[0] + n[1] * n[1]);
				n[0] /= len;
				n[1] /= len;
				double[] n1 = calcIntersection(firstx, firsty, last, n);
				maybeLineTo(path, n1[0], n1[1]);
				n1 = calcIntersection(firstx, firsty, n, firstNormal);
				maybeLineTo(path, n1[0], n1[1]);
				path.closePath();
				break;
			case Round:
				double[] p1 = new double[] { firstx + last[0] * offset, firsty + last[1] * offset };
				maybeLineTo(path, p1[0], p1[1]);
				double[] p2 = new double[] { firstx + firstNormal[0] * offset, firsty + firstNormal[1] * offset };
				double[] midp = new double[] { p1[0] + firstNormal[0] * offset, p1[1] + firstNormal[1] * offset };
				path.quadTo(midp[0], midp[1], p2[0], p2[1]);
				path.closePath();
				break;
			case Standard:
				double[] pt = calcIntersection(firstx, firsty, last, firstNormal);
				maybeLineTo(path, pt[0], pt[1]);
				path.closePath();
				break;
		}
	}

	private double[] calculateNormals(LinkedList<double[]> normals, double firstx, double firsty,
			LinkedList<Pair<Integer, double[]>> list) {
		double lastx = 0, lasty = 0;

		double[] last = null;

		for (Pair<Integer, double[]> pair : list) {
			switch (pair.first) {
				case SEG_CLOSE:
					normals.add(last = calcNormal(lastx, lasty, firstx, firsty, last));
					break;
				case SEG_CUBICTO:
					normals.add(last = calcNormal(lastx, lasty, pair.second[0], pair.second[1], last));
					normals
						.add(last = calcNormal(pair.second[0], pair.second[1], pair.second[2], pair.second[3], last));
					normals
						.add(last = calcNormal(pair.second[2], pair.second[3], pair.second[4], pair.second[5], last));
					lastx = pair.second[4];
					lasty = pair.second[5];
					break;
				case SEG_LINETO:
					normals.add(last = calcNormal(lastx, lasty, pair.second[0], pair.second[1], last));
					lastx = pair.second[0];
					lasty = pair.second[1];
					break;
				case SEG_MOVETO:
					lastx = pair.second[0];
					lasty = pair.second[1];
					break;
				case SEG_QUADTO:
					normals.add(last = calcNormal(lastx, lasty, pair.second[0], pair.second[1], last));
					normals
						.add(last = calcNormal(pair.second[0], pair.second[1], pair.second[2], pair.second[3], last));
					lastx = pair.second[2];
					lasty = pair.second[3];
					break;
			}
		}

		clearNulls(normals);
		return last;
	}

	private static void maybeLineTo(Path2D path, double x, double y) {
		if (Double.isNaN(x) || Double.isNaN(y) || Double.isInfinite(x) || Double.isInfinite(y)) {
			LOG.debug("NaN/Infinity detected!");
			return;
		}
		path.lineTo(x, y);
	}

}
