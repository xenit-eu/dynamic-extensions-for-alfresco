package com.github.dynamicextensionsalfresco.blueprint;

import java.io.IOException;
import java.util.Map;

import com.github.dynamicextensionsalfresco.annotations.AlfrescoPlatform;

import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.util.VersionNumber;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.StringUtils;

/**
 * {@link ClassPathBeanDefinitionScanner} that uses the {@link AlfrescoPlatform} annotation for filtering candidate
 * components.
 * 
 * @author Laurens Fridael
 * 
 */
class AlfrescoPlatformBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

	private final Descriptor descriptor;

	public AlfrescoPlatformBeanDefinitionScanner(final BeanDefinitionRegistry registry, final Descriptor descriptor) {
		super(registry, true);
		this.descriptor = descriptor;
	}

	@Override
	protected boolean isCandidateComponent(final MetadataReader metadataReader) throws IOException {
		return super.isCandidateComponent(metadataReader) && matchesAlfrescoVersion(metadataReader);
	}

	private boolean matchesAlfrescoVersion(final MetadataReader metadataReader) {
		boolean matches = true;
		final AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
		if (metadata.isAnnotated(AlfrescoPlatform.class.getName())) {
			final Map<String, Object> alfrescoVersion = metadata.getAnnotationAttributes(AlfrescoPlatform.class
					.getName());
			final String minVersion = (String) alfrescoVersion.get("minVersion");
			final String maxVersion = (String) alfrescoVersion.get("maxVersion");
			final VersionNumber versionNumber = descriptor.getVersionNumber();
			if (StringUtils.hasText(minVersion) && versionNumber.compareTo(new VersionNumber(minVersion)) < 0) {
				matches = false;
			} else if (StringUtils.hasText(maxVersion) && versionNumber.compareTo(new VersionNumber(maxVersion)) > 0) {
				matches = false;
			}
		}
		return matches;
	}
}
