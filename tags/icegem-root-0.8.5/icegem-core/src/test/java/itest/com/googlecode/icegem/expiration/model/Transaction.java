package itest.com.googlecode.icegem.expiration.model;

import java.io.Serializable;

/**
 * The simple class to emulated transaction behaviour.
 */
public class Transaction implements Serializable {
	private static final long serialVersionUID = 7930238272016220838L;

	private long startedAt = -1;
	private long finishedAt = -1;

	public Transaction() {
	}

	public void begin() {
		startedAt = System.currentTimeMillis();
	}

	public void commit() {
		finishedAt = System.currentTimeMillis();
	}

	public void rollback() {
	}

	public boolean isProcessedSuccessfully() {
		return ((startedAt > -1) && (finishedAt > -1));
	}

	public long getFinishedAt() {
		return finishedAt;
	}
}
