package itest.com.googlecode.icegem.expiration.model;

import java.io.Serializable;

/**
 * The simple class to emulate transaction processing error.
 */
public class TransactionProcessingError implements Serializable {

	private static final long serialVersionUID = -7454231838730568325L;

	private String message;
	private long resolvedAt = -1;

	public TransactionProcessingError() {
	}

	public TransactionProcessingError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setResolved() {
		resolvedAt = System.currentTimeMillis();
	}

	public boolean isResolved() {
		return (resolvedAt > -1);
	}

	public long getResolvedAt() {
		return resolvedAt;
	}

}
