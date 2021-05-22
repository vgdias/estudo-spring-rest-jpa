package org.example.api.rest.api.exceptionhandler;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.EntidadeEmUsoException;
import org.example.api.rest.domain.exception.EntidadeNaoEncontradaException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<?> tratarEntidadeNaoEncontradaException(EntidadeNaoEncontradaException ex, 
			WebRequest request) {

		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), 
				HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<?> tratarEntidadeEmUsoException(EntidadeEmUsoException ex,
			WebRequest request) {

		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), 
				HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(DependenciaNaoEncontradaException.class)
	public ResponseEntity<?> tratarDependenciaNaoEncontradaException(DependenciaNaoEncontradaException ex,
			WebRequest request) {

		return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), 
				HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * Customiza a mensagem da excecao retornada no body de todas as excecoes.
	 * As excecoes internas do Spring retornam null no body, por isso recebem a mensagem
	 * com a causa da excecao. As excecoes da aplicacao retornam uma String com a mensagem
	 * de erro, que eh inserida no body.
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		if (Objects.isNull(body)) {
			body = ExceptionMessage.builder()
					.dataHora(LocalDateTime.now())
					.mensagem(status.getReasonPhrase()).build();
		} else if (body instanceof String) {
			body = ExceptionMessage.builder()
					.dataHora(LocalDateTime.now())
					.mensagem((String) body).build();
		}

		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
}