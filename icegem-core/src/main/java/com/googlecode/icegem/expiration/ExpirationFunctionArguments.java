package com.googlecode.icegem.expiration;

import java.io.Serializable;

/**
 * The container for the arguments for ExpirationFunction.
 */
public class ExpirationFunctionArguments implements Serializable {

	private static final long serialVersionUID = 4578793231858414476L;

	private long packetSize;
	private long packetDelay;

	public ExpirationFunctionArguments(long packetSize, long packetDelay) {
		if (packetSize < 1) {
			throw new IllegalArgumentException("Incorrect packetSize = "
				+ packetSize + ". It should be greater than 0.");
		}

		if (packetDelay < 0) {
			throw new IllegalArgumentException("Incorrect packetDelay = "
				+ packetDelay + ". It should be positive.");
		}

		this.packetSize = packetSize;
		this.packetDelay = packetDelay;
	}

	public long getPacketSize() {
		return packetSize;
	}

	public long getPacketDelay() {
		return packetDelay;
	}

}
