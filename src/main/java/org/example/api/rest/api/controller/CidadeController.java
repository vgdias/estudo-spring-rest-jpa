package org.example.api.rest.api.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.cidade.CidadeInputDto;
import org.example.api.rest.api.model.dto.cidade.CidadeOutputDto;
import org.example.api.rest.domain.exception.ValidacaoException;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.service.CadastroCidadeService;
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
@RequestMapping("/cidades")
public class CidadeController {

	@Autowired
	private CadastroCidadeService cadastroCidadeService;

	@Autowired
	private SmartValidator validator;

	@GetMapping()
	public List<CidadeOutputDto> todas() {
		List<Cidade> cidades = cadastroCidadeService.listar();
		return GenericMapper.collectionMap(cidades, CidadeOutputDto.class);
	}

	@GetMapping("/{id}")
	public CidadeOutputDto buscar(@PathVariable("id") @Positive Long cidadeId) {
		Cidade cidade = cadastroCidadeService.buscar(cidadeId);
		return GenericMapper.map(cidade, CidadeOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeOutputDto adicionar(@Valid @RequestBody CidadeInputDto cidadeNova) {
		Cidade cidadeAdicionada = cadastroCidadeService.adicionar(GenericMapper.map(cidadeNova, Cidade.class));
		return GenericMapper.map(cidadeAdicionada, CidadeOutputDto.class);
	}

	@PutMapping("/{id}")
	public CidadeOutputDto alterar(@PathVariable("id") @Positive Long cidadeAtualId, 
			@RequestBody Map<String, Object> propriedadesCidadeNova, HttpServletRequest request) {

		this.validate(propriedadesCidadeNova);
		Cidade cidadeAtual = cadastroCidadeService.obterCidade(cidadeAtualId);
		GenericMapper.map(propriedadesCidadeNova, cidadeAtual, Cidade.class, request);
		this.validate(cidadeAtual, "cidade");

		Cidade cidadeAtualizado = cadastroCidadeService.alterar(cidadeAtual);
		return GenericMapper.map(cidadeAtualizado, CidadeOutputDto.class);
	}
	private void validate(Map<String, Object> propriedadesCidadeNova) {
		if (propriedadesCidadeNova.isEmpty()) {
			throw new ValidationException("Nenhum argumento fornecido");
		}
		if (propriedadesCidadeNova.containsKey("id")) {
			throw new ValidationException("A propriedade 'id' nao pode ser alterada");
		}
		if (propriedadesCidadeNova.containsKey("estado")) {
			if (((Map<?, ?>)propriedadesCidadeNova.get("estado")).containsKey("nome")) {
				throw new ValidationException("A propriedade 'estado.nome' nao pode ser alterada");
			}
		}
	}
	private void validate(Object object, String objectName) {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, bindingResult, Groups.AlterarCidade.class);
		if (bindingResult.hasErrors()) {
			throw new ValidacaoException(bindingResult);
		}
	} 

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") Long cidadeId) {
		cadastroCidadeService.remover(cidadeId);
	}
}
