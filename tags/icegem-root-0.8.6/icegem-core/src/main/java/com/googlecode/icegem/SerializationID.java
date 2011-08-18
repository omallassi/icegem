/**
 * 
 */
package com.googlecode.icegem;

import com.googlecode.icegem.serialization.serializers.JodaTimeDataSerializer;
import com.googlecode.icegem.serialization.serializers.TimestampDataSerializer;
import com.googlecode.icegem.serialization.serializers.UUIDDataSerializer;

/**
 * is a collection of DataSerializable class IDs used by ICEGEM library.
 * 
 * @author Alexey Kharlamov <aharlamov@gmail.com>
 *
 */
public interface SerializationID {
	/**
	 * Base offset of serialization IDs.
	 */
	int BASE = 16000;
	
    /**
	 * {@link JodaTimeDataSerializer}
	 */
    int JODA_TIME_DATA_SERIALIZER_ID = BASE + 1;

    /**
	 * {@link TimestampDataSerializer}
	 */
    int TIMESTAMP_DATA_SERIALIZER_ID = BASE + 2;

    /**
	 * {@link UUIDDataSerializer}
	 */
    int UUID_DATA_SERIALIZER_ID = BASE + 3;
}
