package org.deegree.protocol.csw.client;

import org.deegree.commons.ows.metadata.OperationsMetadata;
import org.deegree.commons.ows.metadata.ServiceIdentification;
import org.deegree.commons.ows.metadata.ServiceProvider;
import org.deegree.commons.ows.metadata.operation.Operation;
import org.deegree.commons.ows.metadata.party.ContactInfo;
import org.deegree.commons.ows.metadata.party.ResponsibleParty;
import org.deegree.commons.tom.primitive.PrimitiveValue;
import org.deegree.commons.utils.test.TestProperties;
import org.deegree.filter.Expression;
import org.deegree.filter.Filter;
import org.deegree.filter.MatchAction;
import org.deegree.filter.Operator;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.comparison.PropertyIsLike;
import org.deegree.filter.expression.Literal;
import org.deegree.filter.expression.ValueReference;
import org.deegree.filter.logical.Or;
import org.deegree.metadata.MetadataRecord;
import org.deegree.protocol.csw.client.getrecords.GetRecordsResponse;
import org.deegree.protocol.ows.exception.OWSExceptionReport;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

import static org.deegree.commons.xml.CommonNamespaces.APISO;
import static org.deegree.commons.xml.CommonNamespaces.APISO_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * JUnit class tests the functionality of the CSW client. You need to set demo_csw_url in
 * $HOME/.deegree-test.properties to a CSW server getCapabilities URL for this test to
 * work.
 *
 */
public class CSWClientTest {

	@Test
	public void testMetadata() throws OWSExceptionReport, XMLStreamException, IOException {

		String demoCSWURL = TestProperties.getProperty("demo_csw_url");
		assumeTrue(demoCSWURL != null);

		URL serviceUrl = new URL(demoCSWURL);

		CSWClient client = new CSWClient(serviceUrl);
		assertNotNull(client);

		ServiceIdentification serviceId = client.getIdentification();
		assertNotNull(serviceId);
		assertEquals(1, serviceId.getTitles().size());
		assertNotNull(serviceId.getTitles().get(0).getString());
		assertEquals(1, serviceId.getAbstracts().size());
		assertNotNull(serviceId.getAbstracts().get(0).getString());

		assertEquals("CSW", serviceId.getServiceType().getCode());
		assertEquals("2.0.2", serviceId.getServiceTypeVersion().get(0).toString());

		ServiceProvider serviceProvider = client.getProvider();
		assertNotNull(serviceProvider.getProviderName());
		assertNotNull(serviceProvider.getProviderSite());

		ResponsibleParty serviceContact = serviceProvider.getServiceContact();
		assertNotNull(serviceContact.getIndividualName());
		assertNotNull(serviceContact.getPositionName());

		ContactInfo contactInfo = serviceContact.getContactInfo();
		assertNotNull(contactInfo.getPhone().getVoice().get(0));
		assertNotNull(contactInfo.getPhone().getFacsimile().get(0));
		assertNotNull(contactInfo.getAddress().getDeliveryPoint().get(0));
		assertNotNull(contactInfo.getAddress().getCity());
		assertNotNull(contactInfo.getAddress().getAdministrativeArea());
		assertNotNull(contactInfo.getAddress().getPostalCode());
		assertNotNull(contactInfo.getAddress().getCountry());
		assertNotNull(contactInfo.getAddress().getElectronicMailAddress().get(0));
		assertNotNull(contactInfo.getHoursOfService());
		assertNotNull(contactInfo.getContactInstruction());

		assertNotNull(serviceContact.getRole().getCode());

		OperationsMetadata opMetadata = client.getOperations();
		Operation op;

		op = opMetadata.getOperation("GetCapabilities");
		assertNotNull(op.getGetUrls().get(0).toExternalForm());
		assertNotNull(op.getPostUrls().get(0).toExternalForm());

		op = opMetadata.getOperation("DescribeRecord");
		assertNotNull(op.getGetUrls().get(0).toExternalForm());
		assertNotNull(op.getPostUrls().get(0).toExternalForm());

		op = opMetadata.getOperation("GetRecords");
		assertNotNull(op.getGetUrls().get(0).toExternalForm());
		assertNotNull(op.getPostUrls().get(0).toExternalForm());

		op = opMetadata.getOperation("GetRecordById");
		assertNotNull(op.getGetUrls().get(0).toExternalForm());
		assertNotNull(op.getPostUrls().get(0).toExternalForm());

		op = opMetadata.getOperation("Transaction");
		assertNotNull(op);
	}

	private Operator createPropertyLikeFilter(String propertyPrefix, String propertyName, String namespaceURI,
			String value) {
		QName qname = new QName(namespaceURI, propertyName, propertyPrefix);
		Expression param1 = new ValueReference(qname);
		Expression param2 = new Literal<PrimitiveValue>(value);
		Operator rootOperator = new PropertyIsLike(param1, param2, "*", ".", "!", false, MatchAction.ANY);
		return rootOperator;
	}

	@Test
	public void testGetIsoRecords() throws OWSExceptionReport, XMLStreamException, IOException {

		String demoCSWURL = TestProperties.getProperty("demo_csw_url");
		assumeTrue(demoCSWURL != null);

		URL serviceUrl = new URL(demoCSWURL);

		CSWClient client = new CSWClient(serviceUrl);
		assertNotNull(client);

		Operator titleFilter1 = createPropertyLikeFilter(APISO_PREFIX, "Title", APISO, "%e%");
		Operator titleFilter2 = createPropertyLikeFilter(APISO_PREFIX, "Title", APISO, "%a%");
		Operator titleFilter = new Or(titleFilter1, titleFilter2);

		int startPosition = 1;
		int maxRecords = 20;
		Filter constraint = new OperatorFilter(titleFilter);

		GetRecordsResponse recordsResponse = client.getIsoRecords(startPosition, maxRecords, constraint);
		assertNotNull(recordsResponse);
		assertTrue(maxRecords >= recordsResponse.getNumberOfRecordsReturned());
		assertTrue(recordsResponse.getNumberOfRecordsReturned() >= 1);
		assertTrue(recordsResponse.getNumberOfRecordsMatched() >= 1);

		int recordsCounter = 0;

		Iterator<MetadataRecord> iter = recordsResponse.getRecords();
		while (iter.hasNext()) {
			MetadataRecord record = iter.next();
			assertNotNull(record);

			System.out.println(String.format("%s * %s * %s * %s * %s", Arrays.toString(record.getTitle()),
					record.getIdentifier(), record.getLanguage(), record.getModified(), record.getType()));

			assertNotNull(record.getTitle()[0]);
			assertNotNull(record.getAbstract()[0]);
			assertNotNull(record.getIdentifier());
			assertNotNull(record.getLanguage());
			assertNotNull(record.getModified());
			assertNotNull(record.getType());

			recordsCounter++;
		}

		assertTrue(recordsCounter > 0);
	}

}
