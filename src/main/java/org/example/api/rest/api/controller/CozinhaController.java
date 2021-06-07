package org.example.api.rest.api.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.cozinha.CozinhaInputDto;
import org.example.api.rest.api.model.dto.cozinha.CozinhaOutputDto;
import org.example.api.rest.domain.exception.ValidacaoException;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.service.CadastroCozinhaService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/cozinhas")
public class CozinhaController {

	@Autowired
	private CadastroCozinhaService cadastroCozinhaService;

	@Autowired
	private SmartValidator validator;

	@GetMapping()
	public List<CozinhaOutputDto> listar() {
		List<Cozinha> cozinhas = cadastroCozinhaService.listar();
		return GenericMapper.collectionMap(cozinhas, CozinhaOutputDto.class);
	}

	@GetMapping("/{id}")
	public CozinhaOutputDto buscar(@PathVariable("id") @Positive Long cozinhaId) {
		Cozinha cozinha = cadastroCozinhaService.buscar(cozinhaId);
		return GenericMapper.map(cozinha, CozinhaOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CozinhaOutputDto adicionar(@Valid @RequestBody CozinhaInputDto cozinhaNova) {
		Cozinha cozinhaAdicionada = cadastroCozinhaService.adicionar(
				GenericMapper.map(cozinhaNova, Cozinha.class));

		return GenericMapper.map(cozinhaAdicionada, CozinhaOutputDto.class);
	}

	@PutMapping("/alterar/{id}")
	public CozinhaOutputDto alterar(@PathVariable("id") @Positive Long cozinhaAtualId, 
			@RequestBody Map<String, Object> propriedadesCozinhaNova, 
			HttpServletRequest request) {

		Cozinha cozinhaAtual = cadastroCozinhaService.obtemCozinha(cozinhaAtualId);
		GenericMapper.map(propriedadesCozinhaNova, cozinhaAtual, Cozinha.class, request);
		validate(cozinhaAtual, "cozinha");
		Cozinha cozinhaAtualizada = cadastroCozinhaService.alterar(cozinhaAtual);
		return GenericMapper.map(cozinhaAtualizada, CozinhaOutputDto.class);
	}
	private void validate(Cozinha cozinhaAtual, String objectName) {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(cozinhaAtual, objectName);
		validator.validate(cozinhaAtual, bindingResult, Groups.AlterarCozinha.class);
		if (bindingResult.hasErrors()) {
			throw new ValidacaoException(bindingResult);
		}
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive Long cozinhaId) {
		cadastroCozinhaService.remover(cozinhaId);
	}

	@GetMapping("/por-nome")
	public CozinhaOutputDto porNome(@RequestParam @NotBlank String nome) {
		Cozinha cozinha = cadastroCozinhaService.porNome(nome);
		return GenericMapper.map(cozinha, CozinhaOutputDto.class);
	}

	@GetMapping("/com-nome-semelhante")
	public List<CozinhaOutputDto> comNomeSemelhante(@NotBlank String nome) {
		return GenericMapper.collectionMap(cadastroCozinhaService.comNomeSemelhante(nome), CozinhaOutputDto.class);
	}
}
