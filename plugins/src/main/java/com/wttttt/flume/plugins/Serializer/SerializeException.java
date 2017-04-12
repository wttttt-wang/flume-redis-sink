package com.wttttt.flume.plugins.Serializer;

public class SerializeException extends Exception{

	private static final long serialVersionUID = 6172197120384914540L;

	public SerializeException() {
		super();
	}
	public SerializeException(String message) {
	    super(message);
	}

	public SerializeException(String message, Throwable t) {
	    super(message, t);
	}

	public SerializeException(Throwable t) {
	    super(t);
	}
	

}
