package com.huseynovvusal.springblogapi.exception;

public class UserAlreadyRegisteredException extends Exception {

	private static final long serialVersionUID = -6848664979624861169L;

	public UserAlreadyRegisteredException(String message) {
        super(message);
    }

}
