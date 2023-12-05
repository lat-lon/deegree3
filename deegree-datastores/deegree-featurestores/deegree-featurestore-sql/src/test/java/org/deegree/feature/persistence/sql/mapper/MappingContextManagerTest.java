package org.deegree.feature.persistence.sql.mapper;

import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class MappingContextManagerTest {

	@Test
	public void columnName_InLength() throws Exception {
		MappingContextManager mappingContextManager = new MappingContextManager(Collections.<String, String>emptyMap(),
				10, false);
		MappingContext mappingContext = mappingContextManager.newContext(new QName("first"), "id");

		MappingContext parent = mappingContextManager.mapOneToOneElement(mappingContext, new QName("parent"));
		assertEquals(parent.getColumn(), "parent");

		MappingContext shortName = mappingContextManager.mapOneToOneElement(parent, new QName("sn"));
		assertEquals(shortName.getColumn(), "parent_sn");
	}

	@Test
	public void columnName_NotInLength_1() throws Exception {
		MappingContextManager mappingContextManager = new MappingContextManager(Collections.<String, String>emptyMap(),
				10, false);
		MappingContext mappingContext = mappingContextManager.newContext(new QName("first"), "id");
		MappingContext parent = mappingContextManager.mapOneToOneElement(mappingContext, new QName("parent"));

		createMappings(mappingContextManager, parent, "100");

		MappingContext columnLongerWithSmallId = mappingContextManager.mapOneToOneElement(parent, new QName("longer"));
		// parent_longer, last id = 100
		assertEquals(columnLongerWithSmallId.getColumn().length(), 10);
		assertEquals(columnLongerWithSmallId.getColumn(), "parent_101");
	}

	@Test
	public void columnName_NotInLength_2() throws Exception {
		MappingContextManager mappingContextManager = new MappingContextManager(Collections.<String, String>emptyMap(),
				10, false);
		MappingContext mappingContext = mappingContextManager.newContext(new QName("first"), "id");
		MappingContext parent = mappingContextManager.mapOneToOneElement(mappingContext, new QName("parent"));

		createMappings(mappingContextManager, parent, "1000000");

		MappingContext columnLongerWithLargeId = mappingContextManager.mapOneToOneElement(parent, new QName("longer"));
		// parent_longer, last id = 1000000
		assertEquals(columnLongerWithLargeId.getColumn().length(), 10);
		assertEquals(columnLongerWithLargeId.getColumn(), "pa_1000001");
	}

	@Test
	public void columnName_LengthExactlyTheId() throws Exception {
		MappingContextManager mappingContextManager = new MappingContextManager(Collections.<String, String>emptyMap(),
				3, false);
		MappingContext mappingContext = mappingContextManager.newContext(new QName("first"), "id");
		MappingContext parent = mappingContextManager.mapOneToOneElement(mappingContext, new QName("parent"));

		createMappings(mappingContextManager, parent, "99");
		// parent_longer, last id = 100
		MappingContext columnLongerWithLargeId = mappingContextManager.mapOneToOneElement(parent, new QName("longer"));
		assertEquals(columnLongerWithLargeId.getColumn().length(), 3);
		assertEquals(columnLongerWithLargeId.getColumn(), "100");
	}

	@Test
	public void columnName_MaxVerySmall() {
		MappingContextManager mappingContextManager = new MappingContextManager(Collections.<String, String>emptyMap(),
				1, false);
		MappingContext mappingContext = mappingContextManager.newContext(new QName("first"), "id");

		createMappings(mappingContextManager, mappingContext, "9");
		MappingContext first = mappingContextManager.mapOneToOneElement(mappingContext, new QName("first"));
		assertEquals(first.getColumn().length(), 1);
	}

	private void createMappings(MappingContextManager mappingContextManager, MappingContext mappingContext,
			String idOfLastMapping) {
		int indexOfMapping = 1;
		MappingContext newMappingContext;
		do {
			newMappingContext = mappingContextManager.mapOneToOneElement(mappingContext,
					new QName("map" + indexOfMapping++));
		}
		while (!newMappingContext.getColumn().endsWith(idOfLastMapping));
	}

}