/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.commons.ows.metadata.domain;

/**
 * {@link PossibleValues} that are defined by a reference to an externally specified list
 * of all the valid values and/or ranges of values.
 * <p>
 * Data model has been designed to capture the expressiveness of all OWS specifications
 * and versions and was verified against the following specifications:
 * <ul>
 * <li>OWS Common 2.0</li>
 * </ul>
 * </p>
 * <p>
 * From OWS Common 2.0: <cite>Reference to externally specified list of all the valid
 * values and/or ranges of values for this quantity. (Informative: This element was
 * simplified from the metaDataProperty element in GML 3.0.)</cite>
 * </p>
 *
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 */
public class ValuesReference implements PossibleValues {

	private String name;

	private String ref;

	/**
	 * Creates a new {@link ValuesReference} instance.
	 * @param name human-readable name of the list of values, may be <code>null</code>
	 * @param ref reference to externally specified list of all the valid values and/or
	 * ranges of values, may be <code>null</code>
	 */
	public ValuesReference(String name, String ref) {
		this.name = name;
		this.ref = ref;
	}

	/**
	 * Returns the human-readable name of the list of values.
	 * <p>
	 * From OWS Common 2.0: <cite>Human-readable name of the list of values provided by
	 * the referenced document. Can be empty string when this list has no name.</cite>
	 * </p>
	 * @return human-readable name of the list of values, may be <code>null</code>
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the reference to externally specified list of all the valid values and/or
	 * ranges of values.
	 * <p>
	 * From OWS Common 2.0: <cite>Reference to externally specified list of all the valid
	 * values and/or ranges of values for this quantity.</cite>
	 * </p>
	 * @return reference to externally specified list of all the valid values and/or
	 * ranges of values, may be <code>null</code>
	 */
	public String getRef() {
		return ref;
	}

	public void getRef(String ref) {
		this.ref = ref;
	}

}
