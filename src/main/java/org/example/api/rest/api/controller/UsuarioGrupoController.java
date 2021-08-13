package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.grupo.GrupoOutputDto;
import org.example.api.rest.domain.model.Usuario;
import org.example.api.rest.domain.service.UsuarioService;
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
@RequestMapping("/usuarios/{id}/grupos")
public class UsuarioGrupoController {

	@Autowired
	UsuarioService usuarioService;

	@GetMapping
	public List<GrupoOutputDto> listar(
			@PathVariable("id") @Positive(message = "{positive}") Long usuarioId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
		return GenericMapper.collectionMap(usuario.getGrupos(), GrupoOutputDto.class);
	}

	@DeleteMapping("/{grupoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(
			@PathVariable("id") @Positive(message = "{positive}") Long usuarioId, 
			@PathVariable("grupoId") @Positive(message = "{positive}") Long grupoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		usuarioService.excluirGrupo(usuarioId, grupoId);
	}

	@PutMapping("/{grupoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void incluir(
			@PathVariable("id") @Positive(message = "{positive}") Long usuarioId, 
			@PathVariable @Positive(message = "{positive}") Long grupoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		usuarioService.incluirGrupo(usuarioId, grupoId);
	}
}