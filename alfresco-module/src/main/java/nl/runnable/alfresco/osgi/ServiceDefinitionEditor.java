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

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * {@link PropertyEditor} for configuring {@link ServiceDefinition}s in Spring {@link ApplicationContext}s.
 * 
 * @author Laurens Fridael
 * 
 */
public class ServiceDefinitionEditor extends PropertyEditorSupport {

	private static final Pattern BEAN_NAME_AND_SERVICE_NAMES_PATTERN = Pattern
			.compile("(.*?):(.*?)(:(.*?))?(:(\\d+\\.\\d+))?");

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Assert.hasText(text, "Text cannot be empty");
		text = text.trim();
		final Matcher matcher = BEAN_NAME_AND_SERVICE_NAMES_PATTERN.matcher(text);
		if (matcher.matches()) {
			final String[] beanNames = matcher.group(1).split(",");
			final String[] serviceNames = matcher.group(2).split(",");
			final String serviceType = matcher.group(4);
			final String platformVersion = matcher.group(6);
			setValue(new ServiceDefinition(beanNames, serviceNames, serviceType, platformVersion));
		}
	}

}
