package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.cidade.CidadeInputDto;
import org.example.api.rest.api.model.dto.cidade.CidadeOutputDto;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.service.CidadeService;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.example.api.rest.shared.validation.GenericValidator;
import org.example.api.rest.shared.validation.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/cidades")
public class CidadeController {

	@Autowired
	private CidadeService cidadecService;

	@GetMapping()
	public List<CidadeOutputDto> listar(HttpServletRequest request) {
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<Cidade> cidades = cidadecService.listar();
		return GenericMapper.collectionMap(cidades, CidadeOutputDto.class);
	}

	@GetMapping("/{id}")
	public CidadeOutputDto buscarPorId(
			@PathVariable("id") @Positive(message = "{positive}") Long cidadeId,
			HttpServletRequest request) {
		
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList("id")); 
		Cidade cidade = cidadecService.buscarPorId(cidadeId);
		return GenericMapper.map(cidade, CidadeOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeOutputDto adicionar(
			@Valid @RequestBody CidadeInputDto cidadeNovaDto,
			HttpServletRequest request) {
		
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Cidade cidadeNova = GenericMapper.map(cidadeNovaDto, Cidade.class);
		Cidade cidadeAdicionada = cidadecService.adicionar(cidadeNova);
		return GenericMapper.map(cidadeAdicionada, CidadeOutputDto.class);
	}

	@PatchMapping("/{id}")
	public CidadeOutputDto alterar(
			@PathVariable("id") @Positive (message = "{positive}") Long cidadeAtualId, 
			@RequestBody Map<String, Object> propriedadesCidadeNova, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList("id"));
		List<String> propriedadesNaoPermitidas = Arrays.asList("id");
		GenericValidator.validateProperties(propriedadesCidadeNova, propriedadesNaoPermitidas);

		Cidade cidadeAtual = cidadecService.buscarCidadePorId(cidadeAtualId);
		GenericMapper.map(propriedadesCidadeNova, cidadeAtual, Cidade.class, request);
		GenericValidator.validateObject(cidadeAtual, "cidade", Groups.AlterarCidade.class);

		Cidade cidadeAtualizado = cidadecService.alterar(cidadeAtual);
		return GenericMapper.map(cidadeAtualizado, CidadeOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(
			@PathVariable("id") @Positive(message = "{positive}") Long cidadeId, 
			HttpServletRequest request) {
		
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList("id"));
		cidadecService.remover(cidadeId);
	}
}