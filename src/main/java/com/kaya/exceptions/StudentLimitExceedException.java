package com.kaya.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_EXTENDED)
public class StudentLimitExceedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StudentLimitExceedException(String message) {
		super(message);
	}

	
}
