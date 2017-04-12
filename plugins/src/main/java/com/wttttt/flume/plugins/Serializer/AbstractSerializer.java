package com.wttttt.flume.plugins.Serializer;

import org.apache.flume.Event;

public interface AbstractSerializer {

	/**
	 * Serialize event.
	 * @param event
	 * @return serialized data
	 */
	byte[] serialize(Event event) throws SerializeException;

}

