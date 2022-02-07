package com.kaya.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class LessonNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LessonNotFoundException(String message) {
		super(message);
	}

	
}
