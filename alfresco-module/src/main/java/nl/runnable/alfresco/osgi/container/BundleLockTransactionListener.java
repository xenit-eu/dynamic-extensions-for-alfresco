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

import java.util.concurrent.locks.Lock;

import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.repo.transaction.TransactionListenerAdapter;
import org.osgi.framework.BundleListener;
import org.springframework.util.Assert;

/**
 * {@link TransactionListener} that acquires a lock on construction and releases it on commit or rollback. This is
 * mainly intended to synchronize transactions initiated from {@link BundleListener}s.
 * 
 * @deprecated Repository bundle management will be removed in the future.
 */
@Deprecated
class BundleLockTransactionListener extends TransactionListenerAdapter {

	private final Lock lock;

	/**
	 * Constructs an instance and acquires a given {@link Lock}.
	 * 
	 * @param lock
	 */
	public BundleLockTransactionListener(final Lock lock) {
		Assert.notNull(lock, "Lock cannot be null");
		this.lock = lock;
		lock.lock();
	}

	protected void unlock() {
		lock.unlock();
	}

	@Override
	public void afterCommit() {
		unlock();
	}

	@Override
	public void afterRollback() {
		unlock();
	}

}