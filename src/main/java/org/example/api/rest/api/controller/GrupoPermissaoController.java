package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.permissao.PermissaoOutputDto;
import org.example.api.rest.domain.model.Grupo;
import org.example.api.rest.domain.service.GrupoService;
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
@RequestMapping("/grupos/{id}/permissao")
public class GrupoPermissaoController {

	@Autowired
	private GrupoService grupoService;

	@GetMapping
	public List<PermissaoOutputDto> listarGrupoPermissoes(
			@PathVariable("id") @Positive(message = "{positive}") Long grupoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Grupo grupo = grupoService.buscarPorId(grupoId);
		return GenericMapper.collectionMap(grupo.getPermissoes(), PermissaoOutputDto.class);
	}

	@DeleteMapping("/{permissaoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluirGrupoPermissao(
			@PathVariable("id") @Positive(message = "{positive}") Long grupoId, 
			@PathVariable("permissaoId") @Positive(message = "{positive}") Long permissaoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		grupoService.excluirGrupoPermissao(grupoId, permissaoId);
	}

	@PutMapping("/{permissaoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void incluirGrupoPermissao(
			@PathVariable("id") @Positive(message = "{positive}") Long grupoId, 
			@PathVariable @Positive(message = "{positive}") Long permissaoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		grupoService.incluirGrupoPermissao(grupoId, permissaoId);
	}

}