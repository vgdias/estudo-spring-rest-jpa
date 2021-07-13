package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.cozinha.CozinhaInputDto;
import org.example.api.rest.api.model.dto.cozinha.CozinhaOutputDto;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.service.CozinhaService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/cozinhas")
public class CozinhaController {

	@Autowired
	private CozinhaService cozinhaService;

	@GetMapping()
	public List<CozinhaOutputDto> listar(HttpServletRequest request) {
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<Cozinha> cozinhas = cozinhaService.listar();
		return GenericMapper.collectionMap(cozinhas, CozinhaOutputDto.class);
	}

	@GetMapping("/{id}")
	public CozinhaOutputDto buscarPorId(
			@PathVariable("id") @Positive(message = "{positive}") Long cozinhaId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList()); 
		Cozinha cozinha = cozinhaService.buscarPorId(cozinhaId);
		return GenericMapper.map(cozinha, CozinhaOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CozinhaOutputDto adicionar(@Valid @RequestBody CozinhaInputDto cozinhaNovaDto,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList()); 
		Cozinha cozinhaNova = GenericMapper.map(cozinhaNovaDto, Cozinha.class);
		Cozinha cozinhaAdicionada = cozinhaService.adicionar(cozinhaNova);
		return GenericMapper.map(cozinhaAdicionada, CozinhaOutputDto.class);
	}

	@PutMapping("/{id}")
	public CozinhaOutputDto alterar(
			@PathVariable("id") @Positive(message = "{positive}") Long cozinhaAtualId, 
			@RequestBody Map<String, Object> propriedadesCozinhaNova, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<String> propriedadesNaoPermitidas = Arrays.asList("id", "restaurantes");
		GenericValidator.validateProperties(propriedadesCozinhaNova, propriedadesNaoPermitidas);

		Cozinha cozinhaAtual = cozinhaService.buscarCozinhaPorId(cozinhaAtualId);
		GenericMapper.map(propriedadesCozinhaNova, cozinhaAtual, Cozinha.class, request);
		GenericValidator.validateObject(cozinhaAtual, "cozinha", Groups.AlterarCozinha.class);

		Cozinha cozinhaAtualizada = cozinhaService.alterar(cozinhaAtual);
		return GenericMapper.map(cozinhaAtualizada, CozinhaOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive(message = "{positive}") Long cozinhaId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		cozinhaService.remover(cozinhaId);
	}

	@GetMapping("/por-nome")
	public CozinhaOutputDto porNome(@RequestParam @NotBlank(message = "{notBlank}") String nome) {
		Cozinha cozinha = cozinhaService.porNome(nome);
		return GenericMapper.map(cozinha, CozinhaOutputDto.class);
	}

	@GetMapping("/com-nome-semelhante")
	public List<CozinhaOutputDto> comNomeSemelhante(@NotBlank(message = "{notBlank}") String nome) {
		return GenericMapper.collectionMap(cozinhaService.comNomeSemelhante(nome), CozinhaOutputDto.class);
	}
}
