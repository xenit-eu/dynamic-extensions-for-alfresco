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

package nl.runnable.alfresco.webscripts.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.extensions.webscripts.Path;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.util.Assert;

/**
 * {@link Path} implementation for compositions of multiple Paths.
 * 
 * @author Laurens Fridael
 * @see CompositeRegistry
 */
public class CompositePath implements Path {

	private final List<Path> paths = new ArrayList<Path>();

	public void addPath(final Path path) {
		Assert.notNull(path, "Path cannot be null.");
		paths.add(path);
	}

	public void removePath(final Path path) {
		Assert.notNull(path, "Path cannot be null.");
		paths.remove(path);
	}

	@Override
	public String getPath() {
		String result = "";
		for (final Path path : paths) {
			result = path.getPath();
			if (result != null) {
				break;
			}
		}
		return result;
	}

	@Override
	public String getName() {
		String name = "";
		for (final Path path : paths) {
			name = path.getName();
			if (name != null) {
				break;
			}
		}
		return name;
	}

	@Override
	public Path getParent() {
		Path parent = null;
		for (final Path path : paths) {
			parent = path.getParent();
			if (parent != null) {
				break;
			}
		}
		return parent;
	}

	@Override
	public Path[] getChildren() {
		final List<Path> allChildren = new ArrayList<Path>();
		for (final Path path : paths) {
			final Path[] children = path.getChildren();
			if (children != null) {
				allChildren.addAll(Arrays.asList(children));
			}
		}
		return allChildren.toArray(new Path[allChildren.size()]);
	}

	@Override
	public WebScript[] getScripts() {
		final List<WebScript> allScripts = new ArrayList<WebScript>();
		for (final Path path : paths) {
			final WebScript[] scripts = path.getScripts();
			if (scripts != null && scripts.length > 0) {
				allScripts.addAll(Arrays.asList(scripts));
			}
		}
		return allScripts.toArray(new WebScript[allScripts.size()]);
	}

}
