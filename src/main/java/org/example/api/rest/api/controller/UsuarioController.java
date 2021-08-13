package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.usuario.UsuarioOutputDto;
import org.example.api.rest.domain.model.Usuario;
import org.example.api.rest.domain.service.UsuarioService;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.example.api.rest.shared.validation.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping
	public List<UsuarioOutputDto> listar(HttpServletRequest request) {
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<Usuario> usuarios = usuarioService.listar();
		return GenericMapper.collectionMap(usuarios, UsuarioOutputDto.class);
	}

	@GetMapping("/{id}")
	public UsuarioOutputDto buscarPorId(
			@PathVariable("id") @Positive(message = "{positive}") Long usuarioId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList()); 
		Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
		return GenericMapper.map(usuario, UsuarioOutputDto.class);
	}

}