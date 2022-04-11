package com.app.custom_exceptions;

public class InvalidTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidTokenException(String errMsg) {
		super(errMsg);
	}

}
