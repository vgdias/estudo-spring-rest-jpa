package org.example.api.rest.shared.validation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.validation.ValidationException;

import org.example.api.rest.domain.exception.ValidacaoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;

@Component
public class GenericValidator {

	private static SmartValidator validator;

	@Autowired
	private SmartValidator smartValidator;

	@PostConstruct
	public void init() {
		validator = smartValidator;
	}

	public static void validateProperties(Map<String, Object> properties, List<String> deniedProperties) {
		if (properties.isEmpty()) {
			throw new ValidationException("Nenhum argumento fornecido");
		}

		deniedProperties.forEach(property -> {
			if (properties.containsKey(property)) {
				throw new ValidationException(String.format("A propriedade [%s] não pode ser alterada", property));
			}
		});
	}

	public static <T> void validateObject(Object object, String objectName, T validationGroup) {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, bindingResult, validationGroup);
		if (bindingResult.hasErrors()) {
			throw new ValidacaoException(bindingResult);
		}
	}

	public static void validateRequestParams(Enumeration<String> parameters, List<String> validParameters) {
		List<String> unrecognizedParamateres = new ArrayList<>();
		while (parameters.hasMoreElements()) {
			String parameter =  parameters.nextElement();
			if ( ! validParameters.contains(parameter)) {
				unrecognizedParamateres.add(parameter);
			}
		}
		if ( ! unrecognizedParamateres.isEmpty()) {
			String message;
			if (unrecognizedParamateres.size() == 1) {
				message = String.format("Parâmetro [%s] não reconhecido", unrecognizedParamateres.get(0)); 
			} else {
				message = String.format("Parâmetros não reconhecidos: %s", unrecognizedParamateres.toString());
			}
			throw new ValidationException(message);
		}
	}
}