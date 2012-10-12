/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
		assertEquals("1.0", systemPackage.getVersion());
	}
}
