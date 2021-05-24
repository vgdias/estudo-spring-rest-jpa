package org.example.api.rest.api.exceptionhandler;

import java.util.Objects;

import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.EntidadeEmUsoException;
import org.example.api.rest.domain.exception.EntidadeNaoEncontradaException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<?> tratarEntidadeNaoEncontradaException(EntidadeNaoEncontradaException ex, 
			WebRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;
		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Entidade nao encontrada")
				.detail(ex.getMessage())
				.build();
		
		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<?> tratarEntidadeEmUsoException(EntidadeEmUsoException ex,
			WebRequest request) {

		HttpStatus status = HttpStatus.CONFLICT;
		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Entidade em uso")
				.detail(ex.getMessage())
				.build();
		
		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	@ExceptionHandler(DependenciaNaoEncontradaException.class)
	public ResponseEntity<?> tratarDependenciaNaoEncontradaException(DependenciaNaoEncontradaException ex,
			WebRequest request) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Dependencia nao encontrada")
				.detail(ex.getMessage())
				.build();
		
		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Mensagem invalida")
				.detail("O corpo da requisicao possui sintaxe invalida")
				.build();
				
		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
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
					.status(status.value())
					.title(status.getReasonPhrase())
					.build();
		} else if (body instanceof String) {
			body = ExceptionMessage.builder()
					.status(status.value())
					.title((String) body)
					.build();
		}

		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
}