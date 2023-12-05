/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2015 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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
package org.deegree.services.wfs.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TODO add class documentation here
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public class ResponsePagingUtilsTest {

	@Test
	public void testCalculateNextStartIndex_NoFeaturesMatched() {
		int nextStartIndex = ResponsePagingUtils.calculateNextStartIndex(0, 10, 0);
		assertEquals(nextStartIndex, -1);
	}

	@Test
	public void testCalculateNextStartIndex_SecondPage() {
		int nextStartIndex = ResponsePagingUtils.calculateNextStartIndex(0, 10, 97);
		assertEquals(nextStartIndex, 10);
	}

	@Test
	public void testCalculateNextStartIndex_LastPage() {
		int nextStartIndex = ResponsePagingUtils.calculateNextStartIndex(80, 10, 97);
		assertEquals(nextStartIndex, 90);
	}

	@Test
	public void testCalculateNextStartIndex_NextWithOneResult() {
		int nextStartIndex = ResponsePagingUtils.calculateNextStartIndex(91, 5, 97);
		assertEquals(nextStartIndex, 96);
	}

	@Test
	public void testCalculateNextStartIndex_NextNotAvailable() {
		int nextStartIndex = ResponsePagingUtils.calculateNextStartIndex(92, 5, 97);
		assertEquals(nextStartIndex, -1);
	}

	@Test
	public void testCalculatePreviosStartIndex_FirstPage() {
		int nextStartIndex = ResponsePagingUtils.calculatePreviousStartIndex(10, 20);
		assertEquals(nextStartIndex, 0);
	}

	@Test
	public void testCalculatePreviosStartIndex_FirstPageStartIndexLessThanCount() {
		int nextStartIndex = ResponsePagingUtils.calculatePreviousStartIndex(5, 10);
		assertEquals(nextStartIndex, 0);
	}

	@Test
	public void testCalculatePreviosStartIndex_StartIndexGreaterThanCount() {
		int nextStartIndex = ResponsePagingUtils.calculatePreviousStartIndex(30, 10);
		assertEquals(nextStartIndex, 20);
	}

	@Test
	public void testCalculatePreviosStartIndex_StartIndexAtBegin() {
		int nextStartIndex = ResponsePagingUtils.calculatePreviousStartIndex(0, 10);
		assertEquals(nextStartIndex, -1);
	}

}