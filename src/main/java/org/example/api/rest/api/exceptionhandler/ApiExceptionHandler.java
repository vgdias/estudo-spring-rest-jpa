package org.example.api.rest.api.exceptionhandler;

import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
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

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<Object> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex) {

		HttpStatus status = HttpStatus.NOT_FOUND;
		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Entidade nao encontrada")
				.detail(ex.getMessage())
				.build();

		return handleExceptionInternal(ex, exceptionMessage, null, 
				status, null);
	}

	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<Object> handleEntidadeEmUso(EntidadeEmUsoException ex) {

		HttpStatus status = HttpStatus.CONFLICT;
		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Entidade em uso")
				.detail(ex.getMessage())
				.build();

		return handleExceptionInternal(ex, exceptionMessage, null, 
				status, null);
	}

	@ExceptionHandler(DependenciaNaoEncontradaException.class)
	public ResponseEntity<Object> handleDependenciaNaoEncontrada(DependenciaNaoEncontradaException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Dependencia nao encontrada")
				.detail(ex.getMessage())
				.build();

		return handleExceptionInternal(ex, exceptionMessage, null, 
				status, null);
	}

	/**
	 * Customiza as excecoes genericas geradas por erro de sintaxe no corpo da requisicao
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Throwable rootCause = ExceptionUtils.getRootCause(ex);

		if (rootCause instanceof UnrecognizedPropertyException) {
			return handleUnrecognizedPropertyException((UnrecognizedPropertyException) rootCause);
		}

		if (rootCause instanceof InvalidFormatException) {
			return handleInvalidFormatException((InvalidFormatException) rootCause);
		}

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Mensagem invalida")
				.detail("O corpo da requisicao possui sintaxe invalida")
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	@ExceptionHandler(UnrecognizedPropertyException.class)
	private ResponseEntity<Object> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String path = ex.getPath().stream()
				.map(ref -> ref.getFieldName())
				.collect(Collectors.joining("."));
		String detail = String.format("A propriedade '%s' nao foi reconhecida.", path);

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Mensagem invalida")
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, null, 
				status, null);
	}

	/**
	 * Customiza as excecoes genericas geradas por argumento invalido no corpo da requisicao.
	 * Pode ser lancada pelas operacoes de repositorio chamadas pelas classes de servico ou
	 * pelo metodo GenericMapper.map(Object objetoOrigem, Class<RestauranteOutputDto> classeDestino)
	 * ao chamar new ObjectMapper().convertValue(). Por isso, esta sendo tratada separadamente e nao
	 * somente como uma excecao raiz de HttpMessageNotReadableException.
	 * @param ex tipo de excecao a ser tratada
	 * @param headers cabecalhos Http
	 * @param request requisicao Http
	 * @return informacoes de resposta para o usuario
	 */
	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;
		String path = ex.getPath().stream()
				.map(ref -> ref.getFieldName())
				.collect(Collectors.joining("."));
		String detail = String.format("A propriedade '%s' recebeu o valor invalido '%s'. Informe um valor compativel com o tipo '%s'", 
				path , ex.getValue(), ex.getTargetType().getSimpleName());

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Mensagem invalida")
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, null, 
				status, null);
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