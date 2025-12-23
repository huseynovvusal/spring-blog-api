package com.huseynovvusal.springblogapi.exception;

public class BlogNotFoundException extends Exception {

	private static final long serialVersionUID = 4691378901714685149L;
	
	public BlogNotFoundException(String message) {
		super(message);
	}

}
