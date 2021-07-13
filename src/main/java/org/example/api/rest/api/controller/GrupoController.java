package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.grupo.GrupoInputDto;
import org.example.api.rest.api.model.dto.grupo.GrupoOutputDto;
import org.example.api.rest.domain.model.Grupo;
import org.example.api.rest.domain.service.GrupoService;
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
@RequestMapping("/grupos")
public class GrupoController {

	@Autowired
	private GrupoService grupoService;

	@GetMapping
	public List<GrupoOutputDto> listar(HttpServletRequest request) {
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<Grupo> grupos = grupoService.listar();
		return GenericMapper.collectionMap(grupos, GrupoOutputDto.class);
	}

	@GetMapping("/{id}") 
	public GrupoOutputDto buscarPorId(@PathVariable("id") @Positive(message = "{positive}") Long grupoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Grupo grupo = grupoService.buscarPorId(grupoId);
		return GenericMapper.map(grupo, GrupoOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public GrupoOutputDto adicionar( @Valid @RequestBody GrupoInputDto grupoInputDto,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Grupo grupoNovo = GenericMapper.map(grupoInputDto, Grupo.class);
		Grupo grupoAdicionado = grupoService.adicionar(grupoNovo);
		return GenericMapper.map(grupoAdicionado, GrupoOutputDto.class);
	}

	@PatchMapping("/{id}")
	public GrupoOutputDto alterar(@PathVariable("id") @Positive(message = "{positive}") Long grupoId,
			@RequestBody Map<String, Object> propriedadesGrupoNovo,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<String> propriedadesNaoPermitidas = Arrays.asList("id", "permissoes");
		GenericValidator.validateProperties(propriedadesGrupoNovo, propriedadesNaoPermitidas);

		Grupo grupoAtual = grupoService.buscarPorId(grupoId);
		GenericMapper.map(propriedadesGrupoNovo, grupoAtual, Grupo.class, request);
		GenericValidator.validateObject(grupoAtual, "grupo", Groups.AlterarGrupo.class);

		Grupo grupoAtualizado = grupoService.alterar(grupoAtual);
		return GenericMapper.map(grupoAtualizado, GrupoOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive(message = "{positive}") Long grupoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		grupoService.remover(grupoId);
	}
}