package org.example.api.rest.api.exceptionhandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.api.rest.api.exceptionhandler.ExceptionMessage.Field;
import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.EntidadeEmUsoException;
import org.example.api.rest.domain.exception.EntidadeNaoEncontradaException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	/**
	 * Customiza as excecoes genericas que nao foram capturadas por outros handlers 
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Http
	 * @return informacoes de resposta para o usuario
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {

		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;		

		String detail = "Ocorreu um erro interno inesperado no sistema. "
				+ "Tente novamente. Caso o problema persista, entre em contato "
				+ "com o administrador do sistema";

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Erro inesperado")
				.detail(detail)
				.build();

		// Importante colocar o printStackTrace (pelo menos  enquanto nao estou
		// fazendo logging) para mostrar a stacktrace no console
		ex.printStackTrace();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Customiza as excecoes genericas geradas pelo sistema de validacao
	 *
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Http
	 * @return informacoes de resposta para o usuario
	 */
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<Object> handleValidation(ValidationException ex,
			WebRequest request) {

		HttpStatus status = HttpStatus.UNAUTHORIZED;	

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Erro de validacao")
				.detail(ex.getMessage())
				.build();

		if (ex instanceof ConstraintViolationException) {
			return handleConstraintViolation(
					(ConstraintViolationException) ex, status, request);
		}

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Customiza as excecoes geradas por violacao de regra de validacao 
	 * @param ex tipo de excecao a ser tratada 
	 * @param status estado Http
	 * @param request requisicao Htttp
	 * @return informacoes de resposta para o usuario
	 */
	private ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpStatus status,
			WebRequest request) {

		status = HttpStatus.UNAUTHORIZED;	

		List<Field> fields = ex.getConstraintViolations().stream()
				.map(constraintViolation -> ExceptionMessage.Field.builder()
						.name(constraintViolation.getPropertyPath().toString())
						.userMessage(constraintViolation.getMessage().toString())
						.build())
				.collect(Collectors.toList());

		String detail = String.format("Falha na validacao de um ou mais argumentos");

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Operacao nao autorizada")
				.detail(detail)
				.fields(fields)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Customiza as excecoes geradas por entidade nao encontrada no banco de dados 
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Htttp
	 * @return informacoes de resposta para o usuario
	 */
	@ExceptionHandler(EntidadeNaoEncontradaException.class)
	public ResponseEntity<Object> handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex,
			WebRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Recurso invalido")
				.detail(ex.getMessage())
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Customiza as excecoes geradas pela restricao de entidade em uso 
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Htttp
	 * @return informacoes de resposta para o usuario
	 */
	@ExceptionHandler(EntidadeEmUsoException.class)
	public ResponseEntity<Object> handleEntidadeEmUso(EntidadeEmUsoException ex, 
			WebRequest request) {

		HttpStatus status = HttpStatus.CONFLICT;

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Recurso em uso")
				.detail(ex.getMessage())
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Customiza as excecoes geradas por dependencia nao encontrada no banco de dados 
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Htttp
	 * @return informacoes de resposta para o usuario
	 */
	@ExceptionHandler(DependenciaNaoEncontradaException.class)
	public ResponseEntity<Object> handleDependenciaNaoEncontrada(DependenciaNaoEncontradaException ex,
			WebRequest request) {

		HttpStatus status = HttpStatus.FAILED_DEPENDENCY;

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Dependencia invalida")
				.detail(ex.getMessage())
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Customiza as excecoes genericas geradas por requisicao a recurso invalido
	 */
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		if (ex instanceof MethodArgumentTypeMismatchException) {
			return handleMethodArgumentTypeMismatch(
					(MethodArgumentTypeMismatchException) ex, headers, status, request);
		}

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Dados fornecidos invalidos")
				.detail("O corpo da requisicao possui recurso invalido")
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Customiza as excecoes geradas por requisicao com tipo de parametro de URL invalido
	 * @param ex tipo de excecao a ser tratada 
	 * @param headers cabecalho Http a ser inserido na resposta
	 * @param status estado Http
	 * @param request requisicao Http
	 * @return informacoes de resposta para o usuario
	 */
	private ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String detail = String.format("O parametro de URL '%s' recebeu o valor invalido '%s'", 
				ex.getName(), ex.getValue());

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Caminho invalido")
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Customiza as excecoes geradas por requisicao com parametro de URL nao fornecido
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String detail = String.format("O parametro de URL '%s' nao foi fornecido", 
				ex.getParameterName());

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Caminho invalido")
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Customiza as excecoes genericas geradas por sintaxe invalida no corpo da requisicao
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Throwable rootCause = ExceptionUtils.getRootCause(ex);

		if (rootCause instanceof UnrecognizedPropertyException) {
			return handleUnrecognizedProperty((UnrecognizedPropertyException) rootCause,
					headers, request);
		}

		if (rootCause instanceof InvalidFormatException) {
			return handleInvalidFormat((InvalidFormatException) rootCause,
					headers, request);
		}

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Dados fornecidos invalidos")
				.detail("O corpo da requisicao possui sintaxe invalida")
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Customiza as excecoes genericas geradas por argumento desconhecido no corpo da requisicao
	 * Pode ser lancada pelas operacoes de repositorio chamadas pelas classes de servico ou
	 * pelo metodo {@code GenericMapper.map(Object objetoOrigem, Class<RestauranteOutputDto> classeDestino)}
	 * ao chamar {@code new ObjectMapper().convertValue()} 
	 * @param ex tipo de excecao a ser tratada
	 * @param headers cabecalho Http a ser inserido na resposta
	 * @param request requisicao Http
	 * @return informacoes de resposta para o usuario
	 */
	private ResponseEntity<Object> handleUnrecognizedProperty(UnrecognizedPropertyException ex,
			HttpHeaders headers,	WebRequest request) {

		String path = ex.getPath().stream()
				.map(ref -> ref.getFieldName())
				.collect(Collectors.joining("."));

		HttpStatus status = HttpStatus.BAD_REQUEST;
		String detail = String.format("A propriedade '%s' nao foi reconhecida.", path);

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Dados fornecidos invalidos")
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Customiza as excecoes genericas geradas por argumento invalido no corpo da requisicao
	 * Pode ser lancada pelas operacoes de repositorio chamadas pelas classes de servico ou
	 * pelo metodo GenericMapper.map(Object objetoOrigem, Class<RestauranteOutputDto> classeDestino)
	 * ao chamar new ObjectMapper().convertValue(). 
	 * @param ex tipo de excecao a ser tratada
	 * @param headers cabecalho Http a ser inserido na resposta
	 * @param request requisicao Http
	 * @return informacoes de resposta para o usuario
	 */
	private ResponseEntity<Object> handleInvalidFormat(InvalidFormatException ex,
			HttpHeaders headers,	WebRequest request) {

		String path = ex.getPath().stream()
				.map(ref -> ref.getFieldName())
				.collect(Collectors.joining("."));

		HttpStatus status = HttpStatus.BAD_REQUEST;
		String detail = String.format("A propriedade '%s' recebeu o valor invalido '%s'", 
				path , ex.getValue(), ex.getTargetType().getSimpleName());

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Dados fornecidos invalidos")
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Customiza as excecoes geradas por falha de validacao
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, 
			HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		status = HttpStatus.UNAUTHORIZED;	

		List<Field> fields = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> {
					String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
					return ExceptionMessage.Field.builder()
							.name(fieldError.getField())
							.userMessage(message)
							.build();
				})
				.collect(Collectors.toList());

		String detail = String.format("Falha na validacao de um ou mais argumentos");

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Operacao nao autorizada")
				.detail(detail)
				.fields(fields)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Customiza as excecoes geradas por requisicao a recurso inexistente
	 */
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, 
			HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		status = HttpStatus.NOT_FOUND;	
		String detail = String.format("O recurso '%s' nao foi encontrado", ex.getRequestURL());

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Recurso invalido")
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}


	/**
	 * Customiza a mensagem da erro retornada no body de todas as excecoes
	 * As excecoes internas do Spring retornam null no body, por isso recebem a mensagem
	 * com a causa da excecao. As excecoes da aplicacao retornam uma String com a mensagem
	 * de erro, que eh inserida no body
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