package nl.runnable.alfresco.blueprint;

import static org.junit.Assert.*;

import org.junit.Test;

public class ManifestHeaderParserTest {

	private final String exportHeader = "nl.runnable.alfresco.examples;uses:=\"org.alfresco.model,org.springframework.extensions.webscripts,javax.annotation,org.alfresco.service.cmr.repository,org.alfresco.service.cmr.search,nl.runnable.alfresco.webscripts.annotations,nl.runnable.alfresco.annotations,javax.inject,org.alfresco.service.namespace,nl.runnable.alfresco.actions.annotations,org.alfresco.repo.node,nl.runnable.alfresco.behaviours.annotations,org.alfresco.repo.policy,org.alfresco.service.cmr.action\";version=\"1.0.1.SNAPSHOT\",nl.runnable.alfresco.examples.test;version=\"1.0.1.SNAPSHOT\"";

	@Test
	public void testParseExportedPackages() {
		final String[] exportPackages = ManifestHeaderParser.parseExportedPackages(exportHeader);
		assertEquals(2, exportPackages.length);
		assertEquals("nl.runnable.alfresco.examples", exportPackages[0]);
		assertEquals("nl.runnable.alfresco.examples.test", exportPackages[1]);
	}
}
