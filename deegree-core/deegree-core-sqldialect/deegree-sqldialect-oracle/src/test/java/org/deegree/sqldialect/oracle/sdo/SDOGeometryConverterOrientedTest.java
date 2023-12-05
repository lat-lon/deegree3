package org.deegree.sqldialect.oracle.sdo;

import org.deegree.geometry.Geometry;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Scanner;

import static org.deegree.sqldialect.oracle.sdo.SDOGeometryConverterExampleTests.loadFromFile;
import static org.deegree.sqldialect.oracle.sdo.SDOGeometryConverterExampleTests.writeGMLGeometry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SDOGeometryConverterOrientedTest {

	private File sampleSdo = new File("src/test/resources/test/oracle/sdo/oriented/MultiPoint.sdo");

	private File sampleGml = new File("src/test/resources/test/oracle/sdo/oriented/MultiPoint.gml.oriented");

	@Test
	public void testWithOrientationExported() throws Exception {
		SDOGeometryConverter converter = new SDOGeometryConverter();
		converter.setExportOrientedPointAsExtra(true);

		SDOGeometry sdo = loadFromFile(sampleSdo);

		@SuppressWarnings("resource")
		String geomString = new Scanner(sampleGml).useDelimiter("\\Z").next().replace("\r", "").trim();

		Geometry sdoGeom = converter.toGeometry(sdo, null);

		String sdoGeomString = writeGMLGeometry(sdoGeom).replace("\r", "").trim();

		assertNotNull(sdoGeomString, "Convertable 1");
		assertNotNull(geomString, "Convertable 2");
		assertEquals(geomString, sdoGeomString, "SDO -> Geom");
	}

}
