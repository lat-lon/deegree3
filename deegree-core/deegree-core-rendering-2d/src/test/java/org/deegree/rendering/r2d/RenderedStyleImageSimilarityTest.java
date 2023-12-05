/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2022 by:
 - grit graphische Informationstechnik Beratungsgesellschaft mbH -

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

 grit graphische Informationstechnik Beratungsgesellschaft mbH
 Landwehrstr. 143, 59368 Werne
 Germany
 http://www.grit.de/

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
package org.deegree.rendering.r2d;

import org.deegree.commons.utils.FileUtils;
import org.deegree.commons.utils.Triple;
import org.deegree.feature.Feature;
import org.deegree.feature.FeatureCollection;
import org.deegree.feature.types.DynamicAppSchema;
import org.deegree.feature.xpath.TypedObjectNodeXPathEvaluator;
import org.deegree.filter.XPathEvaluator;
import org.deegree.filter.function.FunctionManager;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.Geometry;
import org.deegree.geometry.GeometryFactory;
import org.deegree.geometry.points.Points;
import org.deegree.geometry.standard.points.PackedPoints;
import org.deegree.geometry.utils.GeometryUtils;
import org.deegree.gml.GMLInputFactory;
import org.deegree.gml.GMLStreamReader;
import org.deegree.gml.GMLVersion;
import org.deegree.gml.feature.GMLFeatureReader;
import org.deegree.style.se.parser.SymbologyParser;
import org.deegree.style.se.unevaluated.Style;
import org.deegree.style.styling.Styling;
import org.deegree.style.styling.mark.WellKnownNameManager;
import org.deegree.workspace.Destroyable;
import org.deegree.workspace.Workspace;
import org.deegree.workspace.standard.DefaultWorkspace;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.System.currentTimeMillis;
import static org.deegree.commons.utils.test.IntegrationTestUtils.isImageSimilar;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO: fix dependencies
@Disabled
public class RenderedStyleImageSimilarityTest extends AbstractSimilarityTest {

	private static final File TEST_DIR = new File("src/test/resources/org/deegree/rendering/r2d/similaritytests");

	private static final Logger LOG = LoggerFactory.getLogger(RenderedStyleImageSimilarityTest.class);

	private static Workspace ws = new DefaultWorkspace(TEST_DIR);

	private static List<Destroyable> destroyableResources = new LinkedList<>();

	@BeforeAll
	public static void runBefore() {
		new WellKnownNameManager().init(ws);
		FunctionManager fm = new FunctionManager();
		fm.init(ws);
		destroyableResources.add(fm);
	}

	@AfterAll
	public static void runAfter() {
		destroyableResources.forEach(da -> da.destroy(ws));
	}

	@ParameterizedTest
	@MethodSource("getFiles")
	public void renderAndCompare(String testName, File gmlFile, File styleFile, File imageFile) throws Exception {
		Style style = readStyle(styleFile);
		FeatureCollection fc = readFeatureCollection(gmlFile);
		BufferedImage expected = ImageIO.read(imageFile);

		long time = currentTimeMillis();
		BufferedImage actual = new BufferedImage(expected.getWidth(), expected.getHeight(), TYPE_INT_RGB);
		Graphics2D graphics = actual.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, actual.getWidth(), actual.getHeight());

		Envelope fcEnv = (Envelope) fc.getProperties(GMLFeatureReader.BOUNDED_BY_GML32).get(0).getValue();
		LOG.trace("Envelope: {}", fcEnv);
		Java2DRenderer r = new Java2DRenderer(graphics, actual.getWidth(), actual.getHeight(), fcEnv);
		XPathEvaluator<Feature> evaluator = (XPathEvaluator) new TypedObjectNodeXPathEvaluator();

		for (Feature f : fc) {
			LOG.trace("Rendering Feature {}", f.getId());
			for (Triple<Styling, LinkedList<Geometry>, String> evaluated : style.evaluate(f, evaluator)) {
				for (Geometry evaluatedGeometry : evaluated.second) {
					r.render(evaluated.first, evaluatedGeometry);
				}
			}
		}
		graphics.dispose();
		LOG.debug("Took {} ms", currentTimeMillis() - time);
		assertTrue(isImageSimilar(expected, actual, 0.01, prefixed(testName)),
				"Image for " + testName + "are not similar enough");
	}

	private Stream<Arguments> getFiles() {
		String baseName = TEST_DIR.getAbsolutePath();
		return FileUtils.findFilesForExtensions(TEST_DIR, true, ".gml").stream().map(fGML -> {
			String base = FileUtils.getBasename(fGML.getAbsoluteFile());
			String name = base;
			if (name.startsWith(baseName))
				name = name.substring(baseName.length() + 1);

			File fStyle = new File(base + ".xml");
			File fImg = new File(base + ".png");

			if (fGML.isFile() && fStyle.isFile() && fImg.isFile()) {
				return Arguments.of(name, fGML, fStyle, fImg);
			}
			LOG.warn("Could not find test data same {}.gml/.xml/.png", name);
			return null;
		}).filter(arg -> arg != null);
	}

	private Style readStyle(File file) throws Exception {
		final XMLInputFactory fac = XMLInputFactory.newInstance();
		XMLStreamReader xmlReader = fac.createXMLStreamReader(file.toURI().toURL().toString(),
				file.toURI().toURL().openStream());
		xmlReader.next();
		LOG.debug("Reading style document {}", file);
		Style style = SymbologyParser.INSTANCE.parse(xmlReader);
		return style;
	}

	private FeatureCollection readFeatureCollection(File file) throws Exception {
		GMLVersion version = GMLVersion.GML_32;
		URL docURL = file.toURI().toURL();
		GMLStreamReader gmlStream = GMLInputFactory.createGMLStreamReader(version, docURL);
		gmlStream.setApplicationSchema(new DynamicAppSchema());
		LOG.debug("Populating feature store with features from file '{}'...", docURL);
		FeatureCollection fc = gmlStream.readFeatureCollection();
		return fc;
	}

	public void generateDemoDataForHatching() {
		int objectId = 0;
		int rotation = 0;

		GeometryFactory fac = new GeometryFactory();
		Points points = new PackedPoints(null, new double[] { 10, 10, 10, 90, 90, 90, 90, 10, 10, 10 }, 2);
		Geometry poly = fac.createPolygon("0", null, fac.createLinearRing(null, null, points), null);
		String tpl = "<gml:featureMember><Object gml:id=\"FEATURE_{0}\"><id>{0}</id><rotation>{1}</rotation>";
		tpl = tpl + "<geom><gml:Polygon gml:id=\"GML_{0}\">";
		tpl = tpl + "<gml:exterior><gml:LinearRing><gml:posList>{2}</gml:posList></gml:LinearRing></gml:exterior>";
		tpl = tpl + "</gml:Polygon></geom></Object></gml:featureMember>";

		for (int rowOffset = 0; rowOffset < 10; rowOffset += 1) {
			for (int colOffset = 0; colOffset < 10; colOffset += 1) {
				objectId++;
				rotation += 1;

				Geometry movedGeometry = GeometryUtils.move(poly, colOffset * 100, rowOffset * 100);

				String pointsAsText = movedGeometry.toString() //
					.replace(',', ' ') //
					.replaceAll(".000000", "");
				pointsAsText = pointsAsText.substring(pointsAsText.lastIndexOf('(') + 1, pointsAsText.indexOf(')'));
				System.out.println(MessageFormat.format(tpl, objectId, rotation, pointsAsText));
			}
			System.out.println();
		}
	}

}
