package com.wttttt.flume.plugins.Serializer;

import java.util.Map;

import org.apache.flume.Event;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/***
 * @ClassName:     JsonSerializer.java
 * @Description:   JsonSerializer for serializing event, including headers and body.
 * @author          wttttt
 * @Github https://github.com/wttttt-wang/hadoop_inaction
 * @version         V1.0  
 * @Date           2017.04.11
 */
public class JsonSerializer implements AbstractSerializer{

	public byte[] serialize(Event event) throws SerializeException {
		// TODO: Ability to choose including headers or not. --> by conf
		if (event == null){
			throw new SerializeException("Cannot serialize null event.");
		}
		
		JsonPrimitive body = new JsonPrimitive(new String(event.getBody()));
        JsonObject obj = new JsonObject();
        final Gson gson = new Gson();

        obj.add("body", body);
        if (!event.getHeaders().isEmpty()) {
          JsonObject headers = new JsonObject();
          for (Map.Entry<String, String> header : event.getHeaders().entrySet()) {
            headers.add(header.getKey(), new JsonPrimitive(header.getValue()));
          }
          obj.add("headers", headers);
        }

        return gson.toJson(obj).getBytes();
	}



}
