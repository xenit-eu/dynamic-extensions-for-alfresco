package nl.runnable.alfresco.repository.node.impl;

import nl.runnable.alfresco.repository.node.FileFolderHelper;

import org.alfresco.repo.model.filefolder.HiddenAspect;
import org.springframework.util.Assert;

public class FileFolderHelperImpl implements FileFolderHelper {

	/* Dependencies */

	private HiddenAspect hiddenAspect;

	/* Main operations */

	@Override
	public HiddenAspect getHiddenAspect() {
		return hiddenAspect;
	}

	/* Dependencies */

	public void setHiddenAspect(final HiddenAspect hiddenAspect) {
		Assert.notNull(hiddenAspect);
		this.hiddenAspect = hiddenAspect;
	}

}