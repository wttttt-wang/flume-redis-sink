package com.wttttt.flume.plugins.Serializer;

import org.apache.flume.Event;

public class RawSerializer implements AbstractSerializer{

	public byte[] serialize(Event event) throws SerializeException{
		if (event == null){
			throw new SerializeException("Cannot serialize null event.");
		}
		return event.getBody();
	}

}
