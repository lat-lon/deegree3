/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.sqldialect.filter;

import org.deegree.filter.FilterEvaluationException;
import org.deegree.filter.expression.ValueReference;

import java.util.List;

/**
 * Implementations provide {@link ValueReference} to table/column mappings for
 * {@link AbstractWhereBuilder} implementations.
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 */
public interface PropertyNameMapper {

	/**
	 * Returns the {@link PropertyNameMapping} for the given {@link ValueReference}.
	 * @param propName property name, can be <code>null</code> (indicates that the default
	 * geometry property of the root object is requested)
	 * @param aliasManager manager for creating and tracking table aliases, never
	 * <code>null</code>
	 * @return relational mapping, may be <code>null</code> (if no mapping is possible)
	 * @throws FilterEvaluationException indicates that the {@link ValueReference} is
	 * invalid
	 * @throws UnmappableException
	 */
	PropertyNameMapping getMapping(ValueReference propName, TableAliasManager aliasManager)
			throws FilterEvaluationException, UnmappableException;

	List<PropertyNameMapping> getSpatialMappings(ValueReference propName, TableAliasManager aliasManager)
			throws FilterEvaluationException, UnmappableException;

}
