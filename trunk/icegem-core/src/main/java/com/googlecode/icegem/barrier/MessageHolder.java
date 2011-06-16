/**
 * 
 */
package com.googlecode.icegem.barrier;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.DataSerializer;
import com.gemstone.gemfire.Instantiator;
import com.googlecode.icegem.SerializationID;

/**
 * is a holder used to store message in GemFire delayed delivery region.
 * 
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 * 
 */
public class MessageHolder implements Serializable, DataSerializable {
	static {
		Instantiator.register(new Instantiator(MessageHolder.class, SerializationID.MESSAGE_HOLDER_ID) {
			@Override
			public DataSerializable newInstance() {
				return new MessageHolder();
			}
		});
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String key;

	private long createTime;

	private long timeoutTime;

	private Serializable payload;

	private List<Serializable> trackedEntityKeys;

	/**
	 * 
	 */
	public MessageHolder() {
	}

	public MessageHolder(String barrierName, String payloadId, Serializable payload) {
		this.key = barrierName + "." + payloadId;
		this.payload = payload;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageHolder other = (MessageHolder) obj;
		if (createTime != other.createTime)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (payload == null) {
			if (other.payload != null)
				return false;
		} else if (!payload.equals(other.payload))
			return false;
		if (timeoutTime != other.timeoutTime)
			return false;
		if (trackedEntityKeys == null) {
			if (other.trackedEntityKeys != null)
				return false;
		} else if (!trackedEntityKeys.equals(other.trackedEntityKeys))
			return false;
		return true;
	}

	public void fromData(DataInput din) throws IOException,
			ClassNotFoundException {
		this.key = din.readUTF();
		this.createTime = din.readLong();
		this.timeoutTime = din.readLong();
		this.payload = DataSerializer.readObject(din);
		this.trackedEntityKeys = DataSerializer.readArrayList(din);
	}

	public String getBarrierName() {
		return this.key.substring(0, this.key.indexOf('.'));
	}

	public long getCreateTime() {
		return createTime;
	}

	public String getKey() {
		return this.key;
	}

	public Serializable getPayload() {
		return payload;
	}

	public String getPayloadMessageId() {
		return this.key.substring(key.indexOf('.') + 1);
	}

	public long getTimeoutTime() {
		return timeoutTime;
	}

	public List<Serializable> getTrackedEntityKeys() {
		return trackedEntityKeys;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (createTime ^ (createTime >>> 32));
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
		result = prime * result + (int) (timeoutTime ^ (timeoutTime >>> 32));
		result = prime
				* result
				+ ((trackedEntityKeys == null) ? 0 : trackedEntityKeys
						.hashCode());
		return result;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public void setPayload(Serializable payload) {
		this.payload = payload;
	}

	public void setTimeoutTime(long timeoutTime) {
		this.timeoutTime = timeoutTime;
	}

	public void setTrackedEntityKeys(List<Serializable> trackedEntityKeys) {
		this.trackedEntityKeys = trackedEntityKeys;
	}

	public void toData(DataOutput out) throws IOException {
		out.writeUTF(this.key);
		out.writeLong(this.createTime);
		out.writeLong(this.timeoutTime);
		DataSerializer.writeObject(this.payload, out);
		DataSerializer.writeArrayList((ArrayList<?>) this.trackedEntityKeys,
				out);
	}

	@Override
	public String toString() {
		return "MessageHolder [key=" + key + ", createTime=" + createTime
				+ ", timeoutTime=" + timeoutTime + ", payload=" + payload
				+ ", trackedEntityKeys=" + trackedEntityKeys + "]";
	}
}
