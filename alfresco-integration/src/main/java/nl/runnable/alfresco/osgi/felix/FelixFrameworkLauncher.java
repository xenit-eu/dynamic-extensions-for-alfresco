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

package nl.runnable.alfresco.osgi.felix;

import static org.osgi.framework.Constants.*;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.runnable.alfresco.osgi.BundleEventType;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

/**
 * Example application demonstrating how to launch an embedded OSGI Framework and provision it with bundles from the
 * filesystem.
 * 
 * @author Laurens Fridael
 * 
 */
public class FelixFrameworkLauncher implements Runnable {

	public static void main(final String[] args) {
		final File baseDirectory;
		if (args.length > 0) {
			baseDirectory = new File(args[0]);
		} else {
			baseDirectory = new File("tmp");
		}
		final FelixFrameworkLauncher launcher = new FelixFrameworkLauncher(baseDirectory);
		launcher.run();
	}

	private final File baseDirectory;

	private Framework framework;

	private FelixFrameworkLauncher(final File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	private void launchFramework() throws BundleException {
		System.out.println("Launching OSGI Framework. Using base directory: " + baseDirectory.getAbsolutePath());
		final File cacheDirectory = new File(baseDirectory, "felix-cache");
		if (cacheDirectory.exists()) {
			cacheDirectory.delete();
		}
		final Map<String, String> configuration = new HashMap<String, String>();
		configuration.put(FRAMEWORK_STORAGE, cacheDirectory.getAbsolutePath());
		configuration.put(FRAMEWORK_STORAGE_CLEAN, FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
		framework = new org.apache.felix.framework.FrameworkFactory().newFramework(configuration);
		framework.start();
		framework.getBundleContext().addBundleListener(new BundleListener() {

			@Override
			public void bundleChanged(final BundleEvent event) {
				System.err.println(String.format("---> Bundle %s, state %s", event.getBundle().getSymbolicName(),
						BundleEventType.fromBundleEventTypeId(event.getType())));
			}
		});
	}

	private FileFilter getBundleFileFilter() {
		return new FileFilter() {

			@Override
			public boolean accept(final File file) {
				return file.getName().endsWith(".jar");
			}
		};
	}

	private List<Bundle> installBundles(final File directory) {
		final List<Bundle> bundles = new ArrayList<Bundle>();
		final File[] files = directory.listFiles(getBundleFileFilter());
		for (final File bundleFile : files) {
			try {
				final String location = bundleFile.toURI().toString();
				System.out.println("Installing bundle: " + location);
				final Bundle bundle = framework.getBundleContext().installBundle(location);
				bundles.add(bundle);
			} catch (final BundleException e) {
				throw new RuntimeException(e);
			}
		}
		return bundles;
	}

	protected void startBundles(final List<Bundle> bundles) throws BundleException {
		for (final Bundle bundle : bundles) {
			if (isFragmentBundle(bundle) == false) {
				System.out.println("Starting bundle: " + bundle.getSymbolicName());
				bundle.start(Bundle.START_TRANSIENT);
			}
		}
	}

	private boolean isFragmentBundle(final Bundle bundle) {
		return bundle.getHeaders().get(Constants.FRAGMENT_HOST) != null;
	}

	@Override
	public void run() {
		try {
			final long before = System.currentTimeMillis();
			launchFramework();
			startBundles(installBundles(new File(baseDirectory, "library-bundles")));
			printSeparator();
			startBundles(installBundles(new File(baseDirectory, "framework-bundles")));
			printSeparator();
			startBundles(installBundles(new File(baseDirectory, "application-bundles")));
			printSeparator();
			final long after = System.currentTimeMillis();
			System.out.println(String.format("Time taken: %dms", after - before));
		} catch (final BundleException e) {
			throw new RuntimeException(e);
		}
	}

	protected void printSeparator() {
		System.out.println("--------------------");
	}

}
