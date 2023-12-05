package org.deegree.layer.persistence.feature;

import org.deegree.commons.utils.Pair;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.comparison.PropertyIsEqualTo;
import org.deegree.filter.logical.Or;
import org.deegree.layer.LayerQuery;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class FilterBuilderTest {

	@Test
	public void testBuildRequestFilter() {
		String filterProperty = "type";
		List<String> filterValues = Collections.singletonList("one");

		LayerQuery layerQuery = mockLayerQuery(filterProperty, filterValues);
		Set<QName> propertyNames = createPropertyNames(filterProperty);
		OperatorFilter operatorFilter = FilterBuilder.buildRequestFilter(layerQuery, propertyNames);

		assertInstanceOf(PropertyIsEqualTo.class, operatorFilter.getOperator());
	}

	@Test
	public void testBuildRequestFilter_MultipleValues() {
		String filterProperty = "type";
		List<String> filterValues = Arrays.asList(new String[] { "one", "two" });
		LayerQuery layerQuery = mockLayerQuery(filterProperty, filterValues);
		Set<QName> propertyNames = createPropertyNames(filterProperty);
		OperatorFilter operatorFilter = FilterBuilder.buildRequestFilter(layerQuery, propertyNames);

		assertInstanceOf(Or.class, operatorFilter.getOperator());
	}

	@Test
	public void testBuildRequestFilter_EmptyValues() {
		String filterProperty = "type";
		List<String> filterValues = Collections.emptyList();
		LayerQuery layerQuery = mockLayerQuery(filterProperty, filterValues);
		Set<QName> propertyNames = createPropertyNames(filterProperty);
		OperatorFilter operatorFilter = FilterBuilder.buildRequestFilter(layerQuery, propertyNames);

		assertNull(operatorFilter);
	}

	@Test
	public void testBuildRequestFilter_NullRequestFilter() {
		LayerQuery layerQuery = mockLayerQuery(null);
		Set<QName> propertyNames = createPropertyNames("abc");
		OperatorFilter operatorFilter = FilterBuilder.buildRequestFilter(layerQuery, propertyNames);

		assertNull(operatorFilter);
	}

	@Test
	public void testBuildRequestFilter_PropertyNotKnown() {
		String filterProperty = "type";
		List<String> filterValues = Collections.singletonList("one");

		LayerQuery layerQuery = mockLayerQuery(filterProperty, filterValues);
		Set<QName> propertyNames = createPropertyNames("anothertype");
		OperatorFilter operatorFilter = FilterBuilder.buildRequestFilter(layerQuery, propertyNames);

		assertNull(operatorFilter);
	}

	private LayerQuery mockLayerQuery(String filterProperty, List<String> filterValues) {
		Pair<String, List<String>> requestFilter = new Pair<String, List<String>>(filterProperty, filterValues);

		return mockLayerQuery(requestFilter);
	}

	private LayerQuery mockLayerQuery(Pair<String, List<String>> requestFilter) {
		LayerQuery layerQueryMock = Mockito.mock(LayerQuery.class);
		Mockito.when(layerQueryMock.requestFilter()).thenReturn(requestFilter);
		return layerQueryMock;
	}

	private Set<QName> createPropertyNames(String filterProperty) {
		return Collections.singleton(new QName(filterProperty));
	}

}