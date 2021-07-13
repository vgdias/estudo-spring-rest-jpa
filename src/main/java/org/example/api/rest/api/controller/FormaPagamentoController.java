package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.formapagamento.FormaPagamentoInputDto;
import org.example.api.rest.api.model.dto.formapagamento.FormaPagamentoOutputDto;
import org.example.api.rest.domain.model.FormaPagamento;
import org.example.api.rest.domain.service.FormaPagamentoService;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.example.api.rest.shared.validation.GenericValidator;
import org.example.api.rest.shared.validation.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/formas-pagamento")
public class FormaPagamentoController {

	@Autowired
	private FormaPagamentoService formaPagamentoService;

	@GetMapping()
	public List<FormaPagamentoOutputDto> listar(HttpServletRequest request) {
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<FormaPagamento> formasPagamento = formaPagamentoService.listar();
		return GenericMapper.collectionMap(formasPagamento, FormaPagamentoOutputDto.class);
	}

	@GetMapping("/{id}")
	public FormaPagamentoOutputDto buscarPorId(
			@PathVariable("id") @Positive(message = "{positive}") Long formaPagamentoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		FormaPagamento formaPagamento = formaPagamentoService.buscarPorId(formaPagamentoId);
		return GenericMapper.map(formaPagamento, FormaPagamentoOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FormaPagamentoOutputDto adicionar(
			@Valid @RequestBody FormaPagamentoInputDto formaPagamentoNovaDto,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList()); 
		FormaPagamento formaPagamentoNova = GenericMapper.map(formaPagamentoNovaDto, FormaPagamento.class);
		FormaPagamento formaPagamentoAdicionada = formaPagamentoService.adicionar(formaPagamentoNova);
		return GenericMapper.map(formaPagamentoAdicionada, FormaPagamentoOutputDto.class);
	}

	@PutMapping("/{id}")
	public FormaPagamentoOutputDto alterar(
			@PathVariable("id") @Positive(message = "{positive}") Long formaPagamentoAtualId, 
			@RequestBody Map<String, Object> propriedadesFormaPagamentoNova, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<String> propriedadesNaoPermitidas = Arrays.asList("id");
		GenericValidator.validateProperties(propriedadesFormaPagamentoNova, propriedadesNaoPermitidas);

		FormaPagamento formaPagamentoAtual = formaPagamentoService.buscarFormaPagamentoPorId(formaPagamentoAtualId);
		GenericMapper.map(propriedadesFormaPagamentoNova, formaPagamentoAtual, FormaPagamento.class, request);
		GenericValidator.validateObject(formaPagamentoAtual, "formaPagamento", Groups.AlterarFormaPagamento.class);

		FormaPagamento formaPagamentoAtualizada = formaPagamentoService.alterar(formaPagamentoAtual);
		return GenericMapper.map(formaPagamentoAtualizada, FormaPagamentoOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive(message = "{positive}") Long formaPagamentoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		formaPagamentoService.remover(formaPagamentoId);
	}
}