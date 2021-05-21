package org.example.api.rest.domain.exception;

public class DependenciaNaoEncontradaException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DependenciaNaoEncontradaException(String message) {
		super(message);
	}

}