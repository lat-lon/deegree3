package org.deegree.layer;

import org.deegree.commons.utils.Pair;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.cs.persistence.CRSManager;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.SimpleGeometryFactory;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.deegree.layer.LayerQuery.FILTERPROPERTY;
import static org.deegree.layer.LayerQuery.FILTERVALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class LayerQueryTest {

	@Test
	public void requestFilter() throws Exception {
		String filterProperty = "type";
		String filterValue = "one";
		LayerQuery layerQuery = createLayerQuery(filterProperty, filterValue);

		Pair<String, List<String>> requestFilter = layerQuery.requestFilter();
		assertEquals(requestFilter.getFirst(), filterProperty);
		assertEquals(requestFilter.getSecond().size(), 1);
		assertTrue(requestFilter.getSecond().contains(filterValue));
	}

	@Test
	public void requestFilter_MultipleValues() throws Exception {
		String filterProperty = "type";
		String filterValue = "one, two";
		LayerQuery layerQuery = createLayerQuery(filterProperty, filterValue);

		Pair<String, List<String>> requestFilter = layerQuery.requestFilter();
		assertEquals(requestFilter.getFirst(), filterProperty);
		assertEquals(requestFilter.getSecond().size(), 2);
		assertTrue(requestFilter.getSecond().contains("one"));
		assertTrue(requestFilter.getSecond().contains("two"));
	}

	@Test
	public void requestFilter_MultipleValuesWithMissing() throws Exception {
		String filterProperty = "type";
		String filterValue = "one,,two";
		LayerQuery layerQuery = createLayerQuery(filterProperty, filterValue);

		Pair<String, List<String>> requestFilter = layerQuery.requestFilter();
		assertEquals(requestFilter.getFirst(), filterProperty);
		assertEquals(requestFilter.getSecond().size(), 2);
		assertTrue(requestFilter.getSecond().contains("one"));
		assertTrue(requestFilter.getSecond().contains("two"));
	}

	@Test
	public void requestFilter_NullValue() throws Exception {
		String filterProperty = null;
		String filterValue = "one, two";
		LayerQuery layerQuery = createLayerQuery(filterProperty, filterValue);

		Pair<String, List<String>> requestFilter = layerQuery.requestFilter();
		assertNull(requestFilter);
	}

	@Test
	public void requestFilter_NullProperty() throws Exception {
		String filterProperty = "type";
		String filterValue = null;
		LayerQuery layerQuery = createLayerQuery(filterProperty, filterValue);

		Pair<String, List<String>> requestFilter = layerQuery.requestFilter();
		assertNull(requestFilter);
	}

	@Test
	public void requestFilter_EmptyValue() throws Exception {
		String filterProperty = "";
		String filterValue = "one, two";
		LayerQuery layerQuery = createLayerQuery(filterProperty, filterValue);

		Pair<String, List<String>> requestFilter = layerQuery.requestFilter();
		assertNull(requestFilter);
	}

	@Test
	public void requestFilter_EMptyProperty() throws Exception {
		String filterProperty = "type";
		String filterValue = "";
		LayerQuery layerQuery = createLayerQuery(filterProperty, filterValue);

		Pair<String, List<String>> requestFilter = layerQuery.requestFilter();
		assertNull(requestFilter);
	}

	private LayerQuery createLayerQuery(String filterProperty, String filterValue) throws UnknownCRSException {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(FILTERPROPERTY, filterProperty);
		parameters.put(FILTERVALUE, filterValue);
		Envelope envelope = new SimpleGeometryFactory().createEnvelope(5, 12, 6, 11, CRSManager.lookup("EPSG:4326"));
		return new LayerQuery(envelope, 300, 200, 1, 2, 3, null, null, parameters, null, null, null, 1);
	}

}