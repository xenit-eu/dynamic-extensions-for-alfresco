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

import java.util.Comparator;

import org.osgi.framework.launch.Framework;
import org.springframework.util.Assert;

/**
 * Represents a {@link Framework} system package.
 * 
 * @author Laurens Fridael
 * @see FrameworkConfiguration#setCoreSystemPackages(java.util.Set)
 * @see FrameworkConfiguration#setAdditionalSystemPackages(java.util.Set)
 */
public class SystemPackage {

	public static final String DEFAULT_VERSION = "1.0";

	public static SystemPackage fromString(final String line) {
		final String[] tokens = line.split(";");
		if (tokens.length > 1) {
			return new SystemPackage(tokens[0], tokens[1]);
		} else {
			return new SystemPackage(tokens[0], null);
		}
	}

	public static Comparator<SystemPackage> MOST_SPECIFIC_FIRST = new Comparator<SystemPackage>() {
		@Override
		public int compare(final SystemPackage a, final SystemPackage b) {
			if (a.getName().equals(b.getName()) == false) {
				if (a.getName().startsWith(b.getName())) {
					return -1;
				} else if (b.getName().startsWith(a.getName())) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
	};

	private final String name;

	private final String version;

	public SystemPackage(final String name, final String version) {
		Assert.hasText(name, "Name cannot be empty.");
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		if (version != null) {
			return String.format("%s;%s", name, version);
		} else {
			return name;
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final SystemPackage that = (SystemPackage) o;

		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
