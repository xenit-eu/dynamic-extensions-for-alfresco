package nl.runnable.alfresco.osgi;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link SystemPackageEditor} unit test.
 * 
 * @author Laurens Fridael
 * 
 */
public class SystemPackageEditorTest {

	private SystemPackageEditor systemPackageEditor;

	@Before
	public void setup() {
		systemPackageEditor = new SystemPackageEditor();
	}

	@Test
	public void testSetAsTextWithPackageAndVersion() {
		systemPackageEditor.setAsText("org.alfresco.service.cmr.repository;3.4");
		final SystemPackage systemPackage = (SystemPackage) systemPackageEditor.getValue();
		assertEquals("org.alfresco.service.cmr.repository", systemPackage.getName());
		assertEquals("3.4", systemPackage.getVersion());
	}

	@Test
	public void testSetAsTextWithPackageOnly() {
		systemPackageEditor.setAsText("org.alfresco.service.cmr.repository");
		final SystemPackage systemPackage = (SystemPackage) systemPackageEditor.getValue();
		assertEquals("org.alfresco.service.cmr.repository", systemPackage.getName());
		assertNull(systemPackage.getVersion());
	}
}
