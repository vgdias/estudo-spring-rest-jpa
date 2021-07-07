package org.example.api.rest.api.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.formapagamento.FormaPagamentoInputDto;
import org.example.api.rest.api.model.dto.formapagamento.FormaPagamentoOutputDto;
import org.example.api.rest.domain.exception.ValidacaoException;
import org.example.api.rest.domain.model.FormaPagamento;
import org.example.api.rest.domain.service.CadastroFormaPagamentoService;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.example.api.rest.shared.validation.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
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
	private CadastroFormaPagamentoService cadastroFormaPagamentoService;

	@Autowired
	private SmartValidator validator;

	@GetMapping()
	public List<FormaPagamentoOutputDto> listar() {
		List<FormaPagamento> formasPagamento = cadastroFormaPagamentoService.listar();
		return GenericMapper.collectionMap(formasPagamento, FormaPagamentoOutputDto.class);
	}

	@GetMapping("/{id}")
	public FormaPagamentoOutputDto buscar(
			@PathVariable("id") @Positive(message = "{positive}") Long formaPagamentoId) {
		
		FormaPagamento formaPagamento = cadastroFormaPagamentoService.buscar(formaPagamentoId);
		return GenericMapper.map(formaPagamento, FormaPagamentoOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FormaPagamentoOutputDto adicionar(
			@Valid @RequestBody FormaPagamentoInputDto formaPagamentoNova) {
		
		FormaPagamento formaPagamentoAdicionada = cadastroFormaPagamentoService.adicionar(
				GenericMapper.map(formaPagamentoNova, FormaPagamento.class));

		return GenericMapper.map(formaPagamentoAdicionada, FormaPagamentoOutputDto.class);
	}

	@PutMapping("/{id}")
	public FormaPagamentoOutputDto alterar(
			@PathVariable("id") @Positive(message = "{positive}") Long formaPagamentoAtualId, 
			@RequestBody Map<String, Object> propriedadesFormaPagamentoNova, 
			HttpServletRequest request) {

		this.validate(propriedadesFormaPagamentoNova);
		FormaPagamento formaPagamentoAtual = cadastroFormaPagamentoService.obtemFormaPagamento(formaPagamentoAtualId);
		GenericMapper.map(propriedadesFormaPagamentoNova, formaPagamentoAtual, FormaPagamento.class, request);
		validate(formaPagamentoAtual, "formaPagamento");

		FormaPagamento formaPagamentoAtualizada = cadastroFormaPagamentoService.alterar(formaPagamentoAtual);
		return GenericMapper.map(formaPagamentoAtualizada, FormaPagamentoOutputDto.class);
	}
	private void validate(Map<String, Object> propriedadesFormaPagamentoNova) {
		if (propriedadesFormaPagamentoNova.isEmpty()) {
			throw new ValidationException("Nenhum argumento fornecido");
		}
		if (propriedadesFormaPagamentoNova.containsKey("id")) {
			throw new ValidationException("A propriedade 'id' nao pode ser alterada");
		}
	}
	private void validate(FormaPagamento formaPagamentoAtual, String objectName) {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(formaPagamentoAtual, objectName);
		validator.validate(formaPagamentoAtual, bindingResult, Groups.AlterarFormaPagamento.class);
		if (bindingResult.hasErrors()) {
			throw new ValidacaoException(bindingResult);
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive(message = "{positive}") Long formaPagamentoId) {
		cadastroFormaPagamentoService.remover(formaPagamentoId);
	}
}