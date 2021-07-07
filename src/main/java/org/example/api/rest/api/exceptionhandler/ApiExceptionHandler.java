package org.example.api.rest.api.exceptionhandler;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;
import javax.validation.ValidationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.exception.ValidacaoException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
	 * Excecao lancada por recurso nao encontrado 
	 * 
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Htttp
	 * @return informacoes de resposta ao usuario
	 */
	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<Object> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex,
			WebRequest request) {

		HttpStatus status = HttpStatus.NOT_FOUND;

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title("Recurso nao encontrado")
				.detail(ex.getMessage())
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Excecao lancada por restricao de recurso em uso 
	 * 
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Htttp
	 * @return informacoes de resposta ao usuario
	 */
	@ExceptionHandler(RecursoEmUsoException.class)
	public ResponseEntity<Object> handleRecursoEmUso(RecursoEmUsoException ex, 
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
	 * Excecao lancada por dependencia nao encontrada 
	 * 
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Htttp
	 * @return informacoes de resposta ao usuario
	 */
	@ExceptionHandler(DependenciaNaoEncontradaException.class)
	public ResponseEntity<Object> handleDependenciaNaoEncontrada(DependenciaNaoEncontradaException ex,
			WebRequest request) {

		HttpStatus status = HttpStatus.FAILED_DEPENDENCY;
		String title = "Dependencia nao encontrada";

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(ex.getMessage())
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Excecao lancada por requisicao a recurso invalido
	 */
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		String title = "Dados fornecidos invalidos";
		String detail = "O corpo da requisicao possui recurso invalido";

		if (ex instanceof MethodArgumentTypeMismatchException) {
			return handleMethodArgumentTypeMismatch(
					(MethodArgumentTypeMismatchException) ex, headers, status, request);
		}

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Excecao lancada por requisicao com URL contendo tipo de argumento invalido
	 * 
	 * @param ex tipo de excecao a ser tratada 
	 * @param headers cabecalho Http a ser inserido na resposta
	 * @param status estado Http a ser inserido na resposta
	 * @param request requisicao Http
	 * @return informacoes de resposta ao usuario
	 */
	private ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String title = "Requisição inválida";
		String detail = String.format("Requisição com URL contendo tipo de argumento invalido", 
				ex.getName(), ex.getValue());

		ExceptionMessage.Object error = ExceptionMessage.Object.builder()
				.source(String.format("%s = %s" , ex.getName(), ex.getValue()))
				.rule(String.format("%s deve ser do tipo %s", ex.getName(), ex.getRequiredType().getSimpleName()))
				.build();

		List<ExceptionMessage.Object> errors = Arrays.asList(error);

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.errors(errors)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
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
		String title = "Recurso nao encontrado";
		String detail = String.format("O recurso '%s' nao foi encontrado", ex.getRequestURL());

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Excecao lancada por requisicao contendo parametro de URL nao fornecido
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String title = "Requisição inválida";
		String detail = String.format("O parametro de URL '%s' nao foi fornecido", 
				ex.getParameterName());

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Excecao lancada por sintaxe invalida no corpo da requisicao
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		String title = "Dados fornecidos invalidos";
		String detail = "O corpo da requisicao possui sintaxe invalida";
		Throwable rootCause = ExceptionUtils.getRootCause(ex);

		if (rootCause instanceof UnrecognizedPropertyException) {
			return handleUnrecognizedProperty((UnrecognizedPropertyException) rootCause,
					headers, request, status, title);
		}

		if (rootCause instanceof InvalidFormatException) {
			return handleInvalidFormat((InvalidFormatException) rootCause,
					headers, request, status, title);
		}

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Excecao lancada por falha no mapeamento de propriedade JSON em 
	 * propriedade de objeto
	 * 
	 * @param ex tipo de excecao a ser tratada
	 * @param headers cabecalho Http a ser inserido na resposta
	 * @param request requisicao Http
	 * @param status estado Http a ser inserido na resposta
	 * @param title titulo da mensagem de erro a ser inserida na resposta
	 * @return informacoes de resposta ao usuario
	 */
	private ResponseEntity<Object> handleUnrecognizedProperty(UnrecognizedPropertyException ex,
			HttpHeaders headers,	WebRequest request, HttpStatus status, String title) {

		String path = ex.getPath().stream()
				.map(ref -> ref.getFieldName())
				.collect(Collectors.joining("."));

		String detail = String.format("A propriedade '%s' nao foi reconhecida", path);

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Excecao lancada por argumento com formato invalido no corpo da 
	 * requisicao
	 * 
	 * @param ex tipo de excecao a ser tratada
	 * @param headers cabecalho Http a ser inserido na resposta
	 * @param request requisicao Http
	 * @param status estado Http a ser inserido na resposta
	 * @param title titulo da mensagem de erro a ser inserida na resposta
	 * @return informacoes de resposta ao usuario
	 */
	private ResponseEntity<Object> handleInvalidFormat(InvalidFormatException ex,
			HttpHeaders headers,	WebRequest request, HttpStatus status, String title) {

		String path = ex.getPath().stream()
				.map(ref -> ref.getFieldName())
				.collect(Collectors.joining("."));

		String detail = String.format("A propriedade '%s' recebeu o valor invalido '%s'", 
				path , ex.getValue(), ex.getTargetType().getSimpleName());

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Excecao lancada por falha em validacao programatica
	 * 
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Htttp
	 * @return informacoes de resposta ao usuario
	 */
	@ExceptionHandler({ ValidacaoException.class })
	public ResponseEntity<Object> handleValidacaoException(ValidacaoException ex, WebRequest request) {
		return handleValidationInternal(ex, ex.getBindingResult(), new HttpHeaders(), 
				HttpStatus.BAD_REQUEST, request);
	}   

	/**
	 * Excecao lancada por falha em validacao de argumento 
	 * anotado com {@code @Valid}
	 * 
	 * @param ex tipo de excecao a ser tratada
	 * @param headers cabecalho Http a ser inserido na resposta
	 * @param status estado Http a ser inserido na resposta
	 * @param request requisicao Http
	 * @return informacoes de resposta ao usuario
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return handleValidationInternal(ex, ex.getBindingResult(), headers, status, request);
	}

	/**
	 * Concentra o tratamento das excecoes lancadas por falhas em validacao 
	 * programatica e validacao de argumento anotado com {@code @Valid}
	 * 
	 * @param ex tipo de excecao a ser tratada
	 * @param bindingResult informacoes dos erros de validacao
	 * @param headers cabecalho Http a ser inserido na resposta
	 * @param status estado Http a ser inserido na resposta
	 * @param request requisicao Http
	 * @return informacoes de resposta ao usuario
	 */
	private ResponseEntity<Object> handleValidationInternal(Exception ex, 
			BindingResult bindingResult,
			HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		String title = "Operacao nao permitida";
		String detail = String.format("Falha na validacao de um ou mais argumentos");

		List<ExceptionMessage.Object> objects = bindingResult.getAllErrors().stream()
				.map(objectError -> {
					String message = messageSource.getMessage(objectError, LocaleContextHolder.getLocale());
					String name = objectError.getObjectName();

					if (objectError instanceof FieldError) {
						name = ((FieldError) objectError).getField();
					}

					return ExceptionMessage.Object.builder()
							.source(name)
							.rule(message)
							.build();
				})
				.collect(Collectors.toList());

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.errors(objects)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, headers, 
				status, request);
	}

	/**
	 * Excecao lancada atraves de validacao por anotacoes de Bean Validation
	 *
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Http
	 * @return informacoes de resposta ao usuario
	 */
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<Object> handleValidation(ValidationException ex,
			WebRequest request) {

		HttpStatus status = HttpStatus.BAD_REQUEST;	
		String title = "Erro de validacao";
		String detail = ex.getMessage();

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.build();

		if (ex instanceof ConstraintViolationException) {
			return handleConstraintViolation(
					(ConstraintViolationException) ex, status, request);
		}

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Excecao lancada por validacao na camada de persistencia gerada 
	 * por violacao de regra de integridade 
	 * 
	 * @param ex tipo de excecao a ser tratada 
	 * @param status estado Http a ser inserido na resposta
	 * @param request requisicao Htttp
	 * @return informacoes de resposta ao usuario
	 */
	private ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpStatus status,
			WebRequest request) {

		List<ExceptionMessage.Object> objects = ex.getConstraintViolations().stream()
				.map(constraintViolation -> {
					String invalidValue = constraintViolation.getInvalidValue().toString();
					String argument = null;
					for (Node node : constraintViolation.getPropertyPath()) {
						argument = node.getName();
					} 
					return ExceptionMessage.Object.builder()
							.source(String.format("%s = %s" , argument, invalidValue))
							.rule(constraintViolation.getMessage().toString())
							.build();
				})
				.collect(Collectors.toList());

		String title = "Operacao nao autorizada";
		String detail = String.format("Falha na validacao de um ou mais argumentos");

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.errors(objects)
				.build();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Excecoes genericas que nao foram capturadas por outros handlers 
	 * 
	 * @param ex tipo de excecao a ser tratada 
	 * @param request requisicao Http
	 * @return informacoes de resposta ao usuario
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaught(Exception ex, WebRequest request) {

		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;		

		String title = "Erro inesperado";
		String detail = "Ocorreu um erro interno inesperado no sistema. "
				+ "Tente novamente. Caso o problema persista, entre em contato "
				+ "com o administrador do sistema";

		ExceptionMessage exceptionMessage = ExceptionMessage.builder()
				.status(status.value())
				.title(title)
				.detail(detail)
				.build();

		// Importante colocar o printStackTrace (pelo menos  enquanto nao estou
		// fazendo logging) para mostrar a stacktrace no console
		ex.printStackTrace();

		return handleExceptionInternal(ex, exceptionMessage, new HttpHeaders(), 
				status, request);
	}

	/**
	 * Metodo chamado por todas as demais excecoes para customizar a resposta 
	 * ao usuario, contendo informacoes do erro e do estado Http.
	 * O corpo da mensagem de resposta das excecoes internas do Spring chegam
	 * com valor null, e por isso recebem o estado Http e a mensagem com a causa da excecao. 
	 * As excecoes que chegam com as informacoes completas no corpo da mensagem,
	 * atraves de uma ExceptionMessage, sao repassadas sem alteracao ao metodo da superclasse.
	 * Caso alguma excecao chegue com o corpo contendo somente a respectiva mensagem de 
	 * erro, esta eh inserida juntamente com o estado Http no corpo da mensagem de resposta ao usuario
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