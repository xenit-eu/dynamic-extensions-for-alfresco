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

package nl.runnable.alfresco.osgi.container;

import java.util.List;

import nl.runnable.alfresco.osgi.BundleManifestInfo;
import nl.runnable.alfresco.osgi.BundleModel;

import org.alfresco.service.cmr.repository.NodeRef;
import org.osgi.framework.Bundle;

/**
 * Defines operations for managing OSGi Bundles as repository nodes.
 * 
 * @author Laurens Fridael
 * 
 */
public interface BundleService {

	/**
	 * Installs Bundles already present in the repository. Intended to be called during initialization.
	 * 
	 * @return The List of bundles that were installed.
	 */
	public List<ManagedBundle> installExistingBundles();

	/**
	 * Obtains {@link BundleManifestInfo} from a Bundle represented by a given {@link NodeRef}.
	 * 
	 * @param nodeRef
	 *            The Bundle NodeRef.
	 * @return The {@link BundleManifestInfo} or null if the manifest could not be parsed or is not OSGI-compliant.
	 */
	public BundleManifestInfo getBundleManifestInfo(final NodeRef nodeRef);

	/**
	 * Checks if a Bundle with a given symbolic name and version is installed.
	 * 
	 * @param symbolicName
	 * @param versionNumber
	 * @return True if the Bundle is installed.
	 * @throws IllegalArgumentException
	 *             If symbolicName is empty or versionNumber is empty or does not match the OSGI version format.
	 */
	public boolean isBundleInstalled(String symbolicName, String version);

	/**
	 * Obtains a {@link ManagedBundle} for a given combination of symbolic name and version.
	 * 
	 * @param symbolicName
	 * @param version
	 * @return The matching {@link ManagedBundle} or null if no match could be found.
	 */
	public ManagedBundle getManagedBundle(String symbolicName, String version);

	/**
	 * Obtains the {@link NodeRef} for the folder of type {@link BundleModel#TYPE_BUNDLE_FOLDER} for the given
	 * {@link BundleType}.
	 * 
	 * @param bundleType
	 * @return The matching NodeRef or null if it could not be found.
	 * @deprecated
	 */
	@Deprecated
	public NodeRef getBundleFolderFor(BundleType bundleType);

	/**
	 * Tests if the {@link Bundle} represented by a given {@link NodeRef} should be started automatically.
	 * 
	 * @param bundleNodeRef
	 * @return True if the Bundle should be started automatically, false if not.
	 */
	public boolean startBundleAutomatically(NodeRef bundleNodeRef);

	/**
	 * Installs a Bundle from a repository node.
	 * 
	 * @param nodeRef
	 *            The repository node.
	 * @return The Bundle that was installed.
	 */
	public ManagedBundle installBundle(final NodeRef nodeRef);

	/**
	 * Updates a Bundle from a repository node. Does nothing if the node was not installed as a Bundle first.
	 * 
	 * @param nodeRef
	 *            The repository node.
	 * @return The Bundle that was updated or null if there is no corresponding Bundle.
	 */
	public ManagedBundle updateBundle(final NodeRef nodeRef);

	/**
	 * Uninstalls a Bundle represented by a repository node. Does nothing if the node was not installed as a Bundle
	 * first.
	 * 
	 * @param nodeRef
	 *            The repository node.
	 * @return The Bundle that was uninstalled or null if there was no corresponding Bundle.
	 */
	public ManagedBundle uninstallBundle(final NodeRef nodeRef);

	/**
	 * Obtains all {@link ManagedBundle}s in the repository.
	 * 
	 * @return A List of {@link ManagedBundle}s.
	 */
	public List<ManagedBundle> getManagedBundles();

}