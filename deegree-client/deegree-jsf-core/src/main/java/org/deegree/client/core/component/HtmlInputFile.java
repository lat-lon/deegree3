/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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
package org.deegree.client.core.component;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.behavior.ClientBehaviorHolder;
import jakarta.faces.context.FacesContext;

import org.deegree.client.core.model.UploadedFile;
import org.deegree.client.core.utils.MessageUtils;

/**
 *
 * UIInput component to enable a file upload in a form. The form must have multipart
 * content (enctype="multipart/form-data")
 *
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 */
@FacesComponent(value = "HtmlInputFile")
public class HtmlInputFile extends UIInput implements ClientBehaviorHolder {

	/**
	 * <p>
	 * The standard component type for this component.
	 * </p>
	 */
	public static final String COMPONENT_TYPE = "HtmlInputFile";

	private static enum AdditionalPropertyKeys {

		styleClass, target, style

	}

	public HtmlInputFile() {
		setRendererType("org.deegree.InputFile");
	}

	/**
	 * @return A comma seperated list of available styleClasses. Default value is
	 * "outputXML".
	 */
	public String getStyleClass() {
		return (String) getStateHelper().eval(AdditionalPropertyKeys.styleClass, "outputXML");

	}

	/**
	 * @param styleClass A comma sepereated list of available style classes, passed
	 * through the class attribute of the component.
	 */
	public void setStyleClass(String styleClass) {
		getStateHelper().put(AdditionalPropertyKeys.styleClass, styleClass);
	}

	/**
	 * @return
	 */
	public String getStyle() {
		return (String) getStateHelper().eval(AdditionalPropertyKeys.style, null);

	}

	/**
	 * @param style
	 */
	public void setStyle(String style) {
		getStateHelper().put(AdditionalPropertyKeys.style, style);
	}

	/**
	 * @return location where to write the downloaded file, must begin at the webapp
	 * directory. If null, the file will be stored in deegree's temp directory
	 */
	public String getTarget() {
		return (String) getStateHelper().eval(AdditionalPropertyKeys.target, null);

	}

	/**
	 * @param target Location where to write the downloaded file, beginning at the webapp
	 * directory. Can be null, if the file should be stored in the webapp directory
	 */
	public void setTarget(String styleClass) {
		getStateHelper().put(AdditionalPropertyKeys.target, styleClass);
	}

	@Override
	protected void validateValue(FacesContext context, Object value) {
		if (!isValid()) {
			return;
		}
		if (isRequired() && isUploadedFileEmpty(value)) {
			FacesMessage message = MessageUtils.getFacesMessage(FacesMessage.SEVERITY_ERROR,
					"org.deegree.client.core.component.HtmlInputFile.REQUIRED", getClientId());
			context.addMessage(getClientId(context), message);

			setValid(false);
			return;
		}

	}

	private boolean isUploadedFileEmpty(Object value) {
		if (value == null) {
			return true;
		}
		if (!(value instanceof UploadedFile)) {
			return true;
		}
		UploadedFile uploadedFile = (UploadedFile) value;
		if (uploadedFile.getFileItem() == null) {
			return true;
		}
		return false;
	}

}
