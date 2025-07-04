/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2014 by:
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
package org.deegree.sqldialect;

import org.deegree.commons.jdbc.TableName;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementations provide the vendor-specific behavior for a spatial DBMS so it can be
 * accessed by deegree.
 *
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 *
 */
public abstract class AbstractSQLDialect implements SQLDialect {

	private static final Logger LOG = getLogger(AbstractSQLDialect.class);

	private char defaultEscapeChar = Character.UNASSIGNED;

	@Override
	public char getLeadingEscapeChar() {
		return defaultEscapeChar;
	}

	@Override
	public char getTailingEscapeChar() {
		return defaultEscapeChar;
	}

	@Override
	public boolean isRowLimitingCapable() {
		return true;
	}

	@Override
	public String getSelectBBox(List<String> columns, List<TableName> tables) {
		if (columns.size() > 1 || tables.size() > 1)
			LOG.warn("Multiple geometry columns are currently not supported for all SQL dialects or "
					+ "tunable deegree.sqldialect.consider-all-geometry-columns is false. Using first.");
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append(getBBoxAggregateSnippet(columns.get(0)));
		sql.append(" FROM ");
		sql.append(tables.get(0));
		return sql.toString();
	}

}
