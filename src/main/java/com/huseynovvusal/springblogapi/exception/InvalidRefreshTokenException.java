package com.huseynovvusal.springblogapi.exception;

public class InvalidRefreshTokenException extends Exception {

	private static final long serialVersionUID = 2346365828917833470L;
	
	public InvalidRefreshTokenException(String message) {
		super(message);
	}

}
