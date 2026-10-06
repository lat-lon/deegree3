package org.deegree.commons.ows.metadata;

import java.util.List;

import org.deegree.commons.tom.ows.CodeType;
import org.deegree.commons.tom.ows.LanguageString;
import org.deegree.commons.utils.Pair;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public interface CapabilitiesServiceIdentification {

	List<LanguageString> getTitles();

	List<LanguageString> getAbstracts();

	List<Pair<List<LanguageString>, CodeType>> getKeywords();

	Object getFees();

	List<String> getAccessConstraints();

}
