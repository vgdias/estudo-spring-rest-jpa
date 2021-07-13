package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.formapagamento.FormaPagamentoOutputDto;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.service.RestauranteService;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.example.api.rest.shared.validation.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/restaurantes/{id}/formas-pagamento")
public class RestauranteFormaPagamentoController {

	@Autowired
	private RestauranteService restauranteService;

	@GetMapping
	public List<FormaPagamentoOutputDto> listarRestauranteFormasPagamento(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Restaurante restaurante = restauranteService.buscarPorId(restauranteId);
		return GenericMapper.collectionMap(restaurante.getFormasPagamento(), FormaPagamentoOutputDto.class);
	}

	@DeleteMapping("/{formaPagamentoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluirRestauranteFormaPagamento(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId, 
			@PathVariable("formaPagamentoId") @Positive(message = "{positive}") Long formaPagamentoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		restauranteService.excluirRestauranteFormaPagamento(restauranteId, formaPagamentoId);
	}

	@PutMapping("/{formaPagamentoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void incluirRestauranteFormaPagamento(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId, 
			@PathVariable @Positive(message = "{positive}") Long formaPagamentoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		restauranteService.incluirRestauranteFormaPagamento(restauranteId, formaPagamentoId);
	}

}
