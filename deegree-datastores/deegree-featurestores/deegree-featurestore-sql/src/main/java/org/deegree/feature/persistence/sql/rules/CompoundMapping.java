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
package org.deegree.feature.persistence.sql.rules;

import org.apache.xerces.xs.XSElementDeclaration;
import org.deegree.feature.persistence.sql.expressions.TableJoin;
import org.deegree.feature.persistence.sql.jaxb.CustomConverterJAXB;
import org.deegree.filter.expression.ValueReference;

import java.util.List;

/**
 * Composite {@link Mapping} of complex particles.
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 */
public class CompoundMapping extends Mapping {

	private final List<Mapping> particles;

	private final XSElementDeclaration elDecl;

	public CompoundMapping(ValueReference path, boolean voidable, List<Mapping> particles, List<TableJoin> tableChange,
			XSElementDeclaration elDecl, CustomConverterJAXB converter) {
		super(path, voidable, tableChange, converter);
		this.particles = particles;
		this.elDecl = elDecl;
	}

	public CompoundMapping(ValueReference path, boolean voidable, List<Mapping> particles, List<TableJoin> tableChange,
			XSElementDeclaration elDecl) {
		super(path, voidable, tableChange, null);
		this.particles = particles;
		this.elDecl = elDecl;
	}

	public List<Mapping> getParticles() {
		return particles;
	}

	public XSElementDeclaration getElementDecl() {
		return elDecl;
	}

	@Override
	public String toString() {
		String s = super.toString();
		s += "{";
		for (Mapping particle : particles) {
			s += particle + ",";
		}
		s += "}";
		return s;
	}

}