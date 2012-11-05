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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarInputStream;

import nl.runnable.alfresco.osgi.BundleManifestInfo;
import nl.runnable.alfresco.osgi.BundleManifestInfoService;
import nl.runnable.alfresco.osgi.BundleModel;
import nl.runnable.alfresco.osgi.BundleState;
import nl.runnable.alfresco.osgi.MissingBundleManifestAttributeException;
import nl.runnable.alfresco.repository.node.NodeHelper;
import nl.runnable.alfresco.repository.query.QueryBuilder;
import nl.runnable.alfresco.repository.query.QueryBuilderFactory;
import nl.runnable.alfresco.repository.query.QueryHelper;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @deprecated Repository bundle management will be removed in the future.
 * @author Laurens Fridael
 * 
 */
@Deprecated
public class RepositoryBundleService implements BundleListener, BundleService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/* Dependencies */

	private Framework framework;

	private ContentService contentService;

	private NodeHelper nodeHelper;

	private NodeService nodeService;

	private TransactionService transactionService;

	private QueryHelper queryHelper;

	private QueryBuilderFactory queryBuilderFactory;

	private BundleHelper bundleHelper;

	private ManagedBundleLocator managedBundleLocator;

	private final BundleManifestInfoService bundleManifestInfoService = new BundleManifestInfoService();

	private RepositoryHelper repositoryHelper;

	/* State */

	private final Map<NodeRef, Bundle> bundlesByNodeRef = new ConcurrentHashMap<NodeRef, Bundle>();

	private final Lock bundleUpdateLock = new ReentrantLock();

	/**
	 * Installs Bundles already present in the repository. Intended to be called during initialization.
	 * 
	 * @return The List of bundles that were installed.
	 */
	@Override
	public List<ManagedBundle> installExistingBundles() {
		if (getTransactionService().getRetryingTransactionHelper() == null) {
			/*
			 * The RetryingTransactionHelper is not available when using a mock TransactionService, which occurs when
			 * running within an integration test. Although this is not recommended practice, we allow for this
			 * situation, because the RetryingTransactionHelper class carries too many third-party dependencies.
			 * (javax.transaction, Spring, Hibernate, possibly more.)
			 */
			if (logger.isInfoEnabled()) {
				logger.info("Retrying TransactionHelper is not available. This is normal when running within an integration test.");
			}
			return Collections.emptyList();
		}
		return getTransactionService().getRetryingTransactionHelper().doInTransaction(
				new RetryingTransactionCallback<List<ManagedBundle>>() {

					@Override
					public List<ManagedBundle> execute() {
						return doInstallExistingBundles();
					}

				}, true);
	}

	private List<ManagedBundle> doInstallExistingBundles() {
		// TODO: Scan and resolve any conflicting Bundles.
		final List<ManagedBundle> bundles = new ArrayList<ManagedBundle>();
		for (final NodeRef bundle : getManagedBundleLocator().getLibraryBundles()) {
			final ManagedBundle bundleInfo = installBundle(bundle);
			if (bundleInfo != null) {
				bundles.add(bundleInfo);
			}
		}
		for (final NodeRef bundle : getManagedBundleLocator().getExtensionBundles()) {
			final ManagedBundle bundleInfo = installBundle(bundle);
			if (bundleInfo != null) {
				bundles.add(bundleInfo);
				getBundleHelper().startBundle(bundleInfo.getBundle());
			}
		}
		return bundles;
	}

	@Override
	public BundleManifestInfo getBundleManifestInfo(final NodeRef nodeRef) {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");

		InputStream in = null;
		BundleManifestInfo bundleManifestInfo = null;
		try {
			final ContentReader reader = getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
			if (reader != null) {
				in = reader.getContentInputStream();
				bundleManifestInfo = getBundleManifestInfoService().parseBundleManifestInfo(new JarInputStream(in));
			}
		} catch (final IOException e) {
			logger.warn("Error reading Bundle information from JAR.", e);
		} catch (final MissingBundleManifestAttributeException e) {
			logger.warn("Bundle attribute missing from JAR manifest: {}. Be sure to use OSGI-compliant Bundles.",
					e.getMissingAttribute());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException e) {
					// Ignore this exception.
				}
			}
		}
		return bundleManifestInfo;
	}

	@Override
	public boolean isBundleInstalled(final String symbolicName, final String versionNumber) {
		Assert.hasText(symbolicName, "Symbolic name cannot be empty.");
		Assert.hasText(versionNumber, "Version cannot be empty.");

		return getManagedBundle(symbolicName, versionNumber) != null;
	}

	@Override
	public ManagedBundle getManagedBundle(final String symbolicName, final String versionNumber) {
		Assert.hasText(symbolicName, "Symbolic name cannot be empty.");
		Assert.hasText(versionNumber, "Version cannot be empty.");

		ManagedBundle bundleInfo = null;
		final Bundle bundle = getBundle(symbolicName, versionNumber);
		if (bundle != null) {
			final QueryBuilder q = getQueryBuilderFactory().createQueryBuilder();
			q.isOfType(BundleModel.TYPE_MANAGED_BUNDLE).and().property(BundleModel.PROP_SYMBOLIC_NAME)
					.matches(symbolicName).and().property(BundleModel.PROP_VERSION).matches(versionNumber);
			final List<NodeRef> managedBundleNodeRefs = getQueryHelper().queryNodeRefs(q.createQuery());
			if (managedBundleNodeRefs.isEmpty() == false) {
				if (managedBundleNodeRefs.size() > 1) {
					logger.warn(
							"Found {} Managed Bundles for symbolic name {} and version {}. Using first available Bundle.",
							new Object[] { managedBundleNodeRefs.size(), symbolicName, versionNumber });
				}
				bundleInfo = new ManagedBundle(managedBundleNodeRefs.get(0), bundle);
			}
		}
		return bundleInfo;
	};

	private Bundle getBundle(final String symbolicName, final String versionNumber) {
		Assert.hasText(symbolicName, "Symbolic name cannot be empty.");
		Assert.hasText(versionNumber, "Version cannot be empty.");

		// TODO: Determine if the OSGI API offers another way of finding installed Bundles by symbolic name and version.
		final Version version = new Version(versionNumber);
		final BundleContext bundleContext = getBundleContext();
		if (bundleContext != null) {
			for (final Bundle bundle : bundleContext.getBundles()) {
				if (symbolicName.equals(bundle.getSymbolicName()) && version.equals(bundle.getVersion())) {
					return bundle;
				}
			}
		}
		return null;
	}

	@Override
	public NodeRef getBundleFolderFor(final BundleType bundleType) {
		Assert.notNull(bundleType, "BundleType cannot be null.");

		final QueryBuilder q = getQueryBuilderFactory().createQueryBuilder();
		q.isOfType(BundleModel.TYPE_BUNDLE_FOLDER).and().property(BundleModel.PROP_BUNDLE_TYPE)
				.matches(bundleType.name());
		final List<NodeRef> bundleFolderNodeRefs = getQueryHelper().queryNodeRefs(q.createQuery());

		NodeRef bundleFolderNodeRef = null;
		if (bundleFolderNodeRefs.isEmpty() == false) {
			/* We always return the first result, even if there are multiple matches available. */
			bundleFolderNodeRef = bundleFolderNodeRefs.get(0);
		}
		return bundleFolderNodeRef;
	}

	/**
	 * Attempts to installs a {@link Bundle} from a repository node.
	 * <p>
	 * If the node's parent folder is not of type {@link BundleModel#TYPE_BUNDLE_FOLDER}, the Bundle is ignored.
	 * Furthermore, nodes that represent working copies are ignored as well.
	 * 
	 * @param nodeRef
	 *            The Bundle node.
	 * @return The Bundle that was installed or null if the Bundle was not installed.
	 */
	@Override
	public ManagedBundle installBundle(final NodeRef nodeRef) {
		Assert.notNull(nodeRef, "NodeRef cannot be null");
		if (getNodeHelper().isOfType(getNodeHelper().getPrimaryParent(nodeRef), BundleModel.TYPE_BUNDLE_FOLDER) == false) {
			throw new IllegalArgumentException(String.format("Parent folder is not of type %s",
					BundleModel.TYPE_BUNDLE_FOLDER));
		}

		InputStream in = null;
		ManagedBundle bundleInfo = null;
		try {
			if (getRepositoryHelper().isJavaArchive(nodeRef) == false) {
				throw new IllegalArgumentException(String.format("Node %s is not a Java archive.", nodeRef));
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Installing Bundle from node {}", nodeRef);
			}
			final ContentReader reader = getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
			in = reader.getContentInputStream();
			AlfrescoTransactionSupport.bindListener(new BundleLockTransactionListener(bundleUpdateLock));
			final Bundle bundle = getBundleContext().installBundle(nodeRef.toString(), in);
			if (logger.isDebugEnabled()) {
				logger.debug("Installed Bundle. ID: {}, Symbolic Name: {}, Version: {}",
						new Object[] { bundle.getBundleId(), bundle.getSymbolicName(), bundle.getVersion().toString() });
			}
			getBundlesByNodeRef().put(nodeRef, bundle);
			bundleInfo = new ManagedBundle(nodeRef, bundle);
			populateManagedBundleMetadata(nodeRef, bundle);
		} catch (final BundleException e) {
			logger.error("Error installing Bundle.", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (final IOException e) {
					// Ignore this exception.
				}
			}
		}
		return bundleInfo;
	}

	/**
	 * Populates managed Bundle nodes with metadata.
	 * <p>
	 * This implementation maps the Bundle-Name, Bundle-Description and Bundle-Vendor headers to properties of the
	 * {@link ContentModel#ASPECT_TITLED} and {@link ContentModel#ASPECT_AUTHOR} aspects.
	 * 
	 * @param nodeRef
	 *            The NodeRef representing the managed Bundle resource.
	 * @param bundle
	 *            The Bundle.
	 */
	protected void populateManagedBundleMetadata(final NodeRef nodeRef, final Bundle bundle) {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");
		Assert.notNull(bundle, "Bundle cannot be null.");

		getNodeService().setType(nodeRef, BundleModel.TYPE_MANAGED_BUNDLE);

		final Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		final Dictionary<String, String> headers = bundle.getHeaders();
		final String bundleName = headers.get(Constants.BUNDLE_NAME);
		final String bundleDescription = headers.get(Constants.BUNDLE_DESCRIPTION);
		if (StringUtils.hasText(bundleName) || StringUtils.hasText(bundleDescription)) {
			if (getNodeService().hasAspect(nodeRef, ContentModel.ASPECT_TITLED) == false) {
				getNodeService().addAspect(nodeRef, ContentModel.ASPECT_TITLED,
						Collections.<QName, Serializable> emptyMap());
			}
			properties.put(ContentModel.PROP_TITLE, bundleName);
			properties.put(ContentModel.PROP_DESCRIPTION, bundleDescription);
		}
		final String bundleVendor = headers.get(Constants.BUNDLE_VENDOR);
		if (StringUtils.hasText(bundleVendor)) {
			if (getNodeService().hasAspect(nodeRef, ContentModel.ASPECT_AUTHOR) == false) {
				getNodeService().addAspect(nodeRef, ContentModel.ASPECT_AUTHOR,
						Collections.<QName, Serializable> emptyMap());
			}
			properties.put(ContentModel.PROP_AUTHOR, bundleVendor);
		}
		properties.put(BundleModel.PROP_SYMBOLIC_NAME, bundle.getSymbolicName());
		properties.put(BundleModel.PROP_VERSION, bundle.getVersion().toString());
		properties.put(BundleModel.PROP_BUNDLE_ID, bundle.getBundleId());
		properties.put(BundleModel.PROP_STATE, BundleState.fromStateId(bundle.getState()).name());
		getNodeService().addProperties(nodeRef, properties);
	}

	/**
	 * This implementation checks if the Bundle's parent folder is of type {@link BundleModel#TYPE_BUNDLE_FOLDER} and if
	 * the {@link BundleModel#PROP_AUTOSTART_BUNDLES} setting is enabled.
	 */
	@Override
	public boolean startBundleAutomatically(final NodeRef nodeRef) {
		Assert.notNull(nodeRef, "NodeRef cannot be null");

		final ChildAssociationRef childAssociationRef = getNodeService().getPrimaryParent(nodeRef);
		final NodeRef folderNodeRef = childAssociationRef.getParentRef();
		final AutoStart autoStart;
		if (getNodeHelper().isOfType(folderNodeRef, BundleModel.TYPE_BUNDLE_FOLDER)) {
			final String autoStartValue = (String) getNodeService().getProperty(folderNodeRef,
					BundleModel.PROP_AUTOSTART_BUNDLES);
			if (StringUtils.hasText(autoStartValue)) {
				autoStart = AutoStart.valueOf(autoStartValue);
			} else {
				autoStart = AutoStart.getDefault();
			}
			return autoStart.isStartBundlesAutomatically();
		} else {
			// Parent folder is not a BundleFolder so ignore.
			return false;
		}
	}

	/**
	 * Updates a Bundle from a repository node. Does nothing if the node was not installed as a Bundle first.
	 * 
	 * @param nodeRef
	 *            The repository node.
	 * @return The Bundle that was updated or null if there is no corresponding Bundle.
	 */
	@Override
	public ManagedBundle updateBundle(final NodeRef nodeRef) {
		Assert.notNull(nodeRef, "NodeRef cannot be null");

		ManagedBundle bundleInfo = null;
		if (getBundlesByNodeRef().containsKey(nodeRef)) {
			final Bundle bundle = getBundlesByNodeRef().get(nodeRef);
			bundleInfo = new ManagedBundle(nodeRef, bundle);
			InputStream in = null;
			try {
				if (getRepositoryHelper().isJavaArchive(nodeRef)) {
					final ContentReader reader = getContentService().getReader(nodeRef, ContentModel.PROP_CONTENT);
					in = reader.getContentInputStream();
					AlfrescoTransactionSupport.bindListener(new BundleLockTransactionListener(bundleUpdateLock));
					if (logger.isDebugEnabled()) {
						logger.debug("Updating Bundle {} from node {}", new Object[] { bundle.getBundleId(), nodeRef });
					}
					bundle.update(in);
					populateManagedBundleMetadata(nodeRef, bundle);
				} else {
					logger.warn(
							"Bundle {} represented by node {} has not been updated because it does not seem to be Java archive.",
							bundle.getBundleId(), nodeRef);
					// TODO: Determine what to do here. Possibly uninstall the Bundle?
				}
			} catch (final BundleException e) {
				logger.error("Error updating Bundle.", e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (final IOException e) {
						// Ignore this exception.
					}
				}
			}
		}
		return bundleInfo;
	}

	/**
	 * Uninstalls a Bundle represented by a repository node. Does nothing if the node was not installed as a Bundle
	 * first.
	 * 
	 * @param nodeRef
	 *            The repository node.
	 * @return The Bundle that was uninstalled or null if there was no corresponding Bundle.
	 */
	@Override
	public ManagedBundle uninstallBundle(final NodeRef nodeRef) {
		Assert.notNull(nodeRef, "NodeRef cannot be null");
		ManagedBundle bundleInfo = null;
		if (getBundlesByNodeRef().containsKey(nodeRef)) {
			final Bundle bundle = getBundlesByNodeRef().remove(nodeRef);
			bundleInfo = new ManagedBundle(nodeRef, bundle);
			AlfrescoTransactionSupport.bindListener(new BundleLockTransactionListener(bundleUpdateLock));
			if (getNodeService().exists(nodeRef)) {
				getNodeService().setType(nodeRef, ContentModel.TYPE_CONTENT);
			}
			try {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Uninstalling Bundle from node {}. ID: {}, Symbolic Name: {}, Version: {}",
							new Object[] { nodeRef, bundle.getBundleId(), bundle.getSymbolicName(), bundle.getVersion() });
				}
				bundle.uninstall();
			} catch (final BundleException e) {
				logger.error("Error uninstalling Bundle.", e);
			}
		}
		return bundleInfo;
	}

	/**
	 * Handles {@link BundleListener} notifications.
	 */
	@Override
	public void bundleChanged(final BundleEvent bundleEvent) {
		Assert.notNull(bundleEvent, "BundleEvent cannot be null.");
		final Bundle bundle = bundleEvent.getBundle();
		if (bundlesByNodeRef.containsValue(bundle) == false) {
			// Not one of the Bundles we installed, ignore.
			return;
		}
		NodeRef nodeRef = null;
		for (final Entry<NodeRef, Bundle> entry : getBundlesByNodeRef().entrySet()) {
			if (bundle.equals(entry.getValue())) {
				nodeRef = entry.getKey();
				break;
			}
		}
		assert (nodeRef != null);
		final BundleState bundleState = BundleState.fromStateId(bundle.getState());
		if (bundleState == null) {
			// Should not happen.
			throw new IllegalStateException("Unhandled Bundle state ID: " + bundle.getState());
		}
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Bundle changed. ID: {}, Symbolic Name: {}, Version: {}, State: {} ",
					new Object[] { bundle.getBundleId(), bundle.getSymbolicName(), bundle.getVersion(),
							bundleState.name() });
		}
		updateBundleState(nodeRef, bundleState);
	}

	/**
	 * Updates node metadata according to Bundle state.
	 * 
	 * @param nodeRef
	 * @param bundleEventType
	 */
	protected void updateBundleState(final NodeRef nodeRef, final BundleState bundleState) {
		Assert.notNull(nodeRef, "NodeRef cannot be null.");
		Assert.notNull(bundleState, "BundleState cannot be null.");
		bundleUpdateLock.lock();
		try {
			AuthenticationUtil.runAs(new RunAsWork<Void>() {

				@Override
				public Void doWork() throws Exception {
					return getTransactionService().getRetryingTransactionHelper().doInTransaction(
							new RetryingTransactionCallback<Void>() {

								@Override
								public Void execute() {
									if (getNodeService().exists(nodeRef)
											&& getNodeHelper().isOfType(nodeRef, BundleModel.TYPE_MANAGED_BUNDLE)) {
										getNodeService().setProperty(nodeRef, BundleModel.PROP_STATE,
												bundleState.name());
									}
									return null;
								}
							});
				}
			}, AuthenticationUtil.getSystemUserName());
		} finally {
			bundleUpdateLock.unlock();
		}
	}

	@Override
	public List<ManagedBundle> getManagedBundles() {
		final QueryBuilder q = getQueryBuilderFactory().createQueryBuilder();
		q.isOfType(BundleModel.TYPE_MANAGED_BUNDLE);
		final List<NodeRef> managedBundleNodeRefs = getQueryHelper().queryNodeRefs(q.createQuery());

		final List<ManagedBundle> managedBundles = new ArrayList<ManagedBundle>();
		final NodeService nodeService = getNodeService();
		for (final NodeRef managedBundleNodeRef : managedBundleNodeRefs) {
			if (nodeService.exists(managedBundleNodeRef)) {
				final String symbolicName = (String) nodeService.getProperty(managedBundleNodeRef,
						BundleModel.PROP_SYMBOLIC_NAME);
				final String version = (String) nodeService.getProperty(managedBundleNodeRef, BundleModel.PROP_VERSION);
				final Bundle bundle = getBundle(symbolicName, version);
				if (bundle != null) {
					managedBundles.add(new ManagedBundle(managedBundleNodeRef, bundle));
				}
			}
		}
		return managedBundles;
	}

	/**
	 * Obtains the {@link NodeRef} for the folder that contains the Dynamic Extensions.
	 * <p>
	 * This implementation looks for folder <code>/Company Home/Data Dictionary/Dynamic Extensions</code>.
	 * 
	 * @return The corresponding {@link NodeRef} or null if it could not be found.
	 */
	protected NodeRef getDynamicExtensionsFolder() {
		return null;
	}

	/* Dependencies */

	@Required
	public void setFramework(final Framework framework) {
		Assert.notNull(framework);
		this.framework = framework;
	}

	protected Framework getFramework() {
		return framework;
	}

	@Required
	public void setContentService(final ContentService contentService) {
		Assert.notNull(contentService);
		this.contentService = contentService;
	}

	protected ContentService getContentService() {
		return contentService;
	}

	@Required
	public void setNodeHelper(final NodeHelper nodeHelper) {
		Assert.notNull(nodeHelper);
		this.nodeHelper = nodeHelper;
	}

	protected NodeHelper getNodeHelper() {
		return nodeHelper;
	}

	@Required
	public void setNodeService(final NodeService nodeService) {
		Assert.notNull(nodeService);
		this.nodeService = nodeService;
	}

	protected NodeService getNodeService() {
		return nodeService;
	}

	@Required
	public void setTransactionService(final TransactionService transactionService) {
		Assert.notNull(transactionService);
		this.transactionService = transactionService;
	}

	protected TransactionService getTransactionService() {
		return transactionService;
	}

	@Required
	public void setQueryBuilderFactory(final QueryBuilderFactory queryBuilderFactory) {
		this.queryBuilderFactory = queryBuilderFactory;
	}

	protected QueryBuilderFactory getQueryBuilderFactory() {
		return queryBuilderFactory;
	}

	@Required
	public void setQueryHelper(final QueryHelper queryHelper) {
		this.queryHelper = queryHelper;
	}

	protected QueryHelper getQueryHelper() {
		return queryHelper;
	}

	@Required
	public void setBundleHelper(final BundleHelper bundleHelper) {
		Assert.notNull(bundleHelper);
		this.bundleHelper = bundleHelper;
	}

	public BundleHelper getBundleHelper() {
		return bundleHelper;
	}

	@Required
	public void setManagedBundleLocator(final ManagedBundleLocator managedBundleLocator) {
		this.managedBundleLocator = managedBundleLocator;
	}

	protected ManagedBundleLocator getManagedBundleLocator() {
		return managedBundleLocator;
	}

	protected BundleManifestInfoService getBundleManifestInfoService() {
		return bundleManifestInfoService;
	}

	@Required
	public void setRepositoryHelper(final RepositoryHelper repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}

	protected RepositoryHelper getRepositoryHelper() {
		return repositoryHelper;
	}

	/* State */

	protected BundleContext getBundleContext() {
		return getFramework().getBundleContext();
	}

	public Map<NodeRef, Bundle> getBundlesByNodeRef() {
		return bundlesByNodeRef;
	}

}