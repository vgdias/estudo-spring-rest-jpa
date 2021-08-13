package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.permissao.PermissaoInputDto;
import org.example.api.rest.api.model.dto.permissao.PermissaoOutputDto;
import org.example.api.rest.domain.model.Permissao;
import org.example.api.rest.domain.service.PermissaoService;
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
@RequestMapping("/permissoes")
public class PermissaoController {

	@Autowired
	private PermissaoService permissaoService;

	@GetMapping()
	public List<PermissaoOutputDto> listar(HttpServletRequest request) {
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<Permissao> permissoes = permissaoService.listar();
		return GenericMapper.collectionMap(permissoes, PermissaoOutputDto.class);
	}

	@GetMapping("/{id}")
	public PermissaoOutputDto buscarPorId(
			@PathVariable("id") @Positive(message = "{positive}") Long permissaoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Permissao permissao = permissaoService.buscarPorId(permissaoId);
		return GenericMapper.map(permissao, PermissaoOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PermissaoOutputDto adicionar(
			@Valid @RequestBody PermissaoInputDto permissaoNovaDto,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList()); 
		Permissao permissaoNova = GenericMapper.map(permissaoNovaDto, Permissao.class);
		Permissao permissaoAdicionada = permissaoService.adicionar(permissaoNova);
		return GenericMapper.map(permissaoAdicionada, PermissaoOutputDto.class);
	}

	@PutMapping("/{id}")
	public PermissaoOutputDto alterar(
			@PathVariable("id") @Positive(message = "{positive}") Long permissaoAtualId, 
			@RequestBody Map<String, Object> propriedadesPermissaoNova, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<String> propriedadesNaoPermitidas = Arrays.asList("id");
		GenericValidator.validateProperties(propriedadesPermissaoNova, propriedadesNaoPermitidas);

		Permissao permissaoAtual = permissaoService.buscarPermissaoPorId(permissaoAtualId);
		GenericMapper.map(propriedadesPermissaoNova, permissaoAtual, Permissao.class, request);
		GenericValidator.validateObject(permissaoAtual, "permissao", Groups.AlterarPermissao.class);

		Permissao permissaoAtualizada = permissaoService.alterar(permissaoAtual);
		return GenericMapper.map(permissaoAtualizada, PermissaoOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive(message = "{positive}") Long permissaoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		permissaoService.remover(permissaoId);
	}
}