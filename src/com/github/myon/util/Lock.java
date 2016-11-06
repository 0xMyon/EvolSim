package com.github.myon.util;

public class Lock {

	private boolean locked;

	/**
	 * Creates a new Lock with the flag set to the value of the parameter locked
	 */
	public Lock(final boolean locked) {
		this.locked = locked;
	}

	/**
	 * Requests the lock. Waits on this object if locked. Sets the lock-flag.
	 */
	public synchronized void lock() {
		while (this.locked) {
			try {
				this.wait();
			} catch (final InterruptedException e) {
				return;
			}
		}
		this.locked = true;
	}

	/**
	 * Releases the lock, i.e. resets the lock-flag. No further action, if no
	 * thread is waiting. If at least one thread is waiting, awakes one of them.
	 * If more than one thread is waiting, the choice is random but fair.
	 */
	public synchronized void unlock() {
		this.locked = false;
		this.notify();
	}
}