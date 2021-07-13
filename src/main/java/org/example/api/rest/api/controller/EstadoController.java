package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.estado.EstadoInputDto;
import org.example.api.rest.api.model.dto.estado.EstadoOutputDto;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.service.EstadoService;
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
@RequestMapping("/estados")
public class EstadoController {

	@Autowired
	private EstadoService estadoService;

	@GetMapping
	public List<EstadoOutputDto> listar(HttpServletRequest request) {
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<Estado> estados = estadoService.listar();
		return GenericMapper.collectionMap(estados, EstadoOutputDto.class);
	}

	@GetMapping("/{id}")
	public EstadoOutputDto buscarPorId(@PathVariable("id") @Positive(message = "{positive}") Long estadoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Estado estadoAtual = estadoService.buscarPorId(estadoId);
		return GenericMapper.map(estadoAtual, EstadoOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoOutputDto adicionar(@Valid @RequestBody EstadoInputDto estadoNovoDto,
			HttpServletRequest request) {

		Estado estadoNovo = GenericMapper.map(estadoNovoDto, Estado.class);
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList()); 
		Estado estadoAdicionado = estadoService.adicionar(estadoNovo);
		return GenericMapper.map(estadoAdicionado, EstadoOutputDto.class);
	}

	@PutMapping("/{id}")
	public EstadoOutputDto alterar(
			@PathVariable("id") @Positive(message = "{positive}") Long estadoAtualId, 
			@RequestBody Map<String, Object> propriedadesEstadoNovo, HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<String> propriedadesNaoPermitidas = Arrays.asList("id");
		GenericValidator.validateProperties(propriedadesEstadoNovo, propriedadesNaoPermitidas);

		Estado estadoAtual = estadoService.obterEstado(estadoAtualId);
		GenericMapper.map(propriedadesEstadoNovo, estadoAtual, Estado.class, request);
		GenericValidator.validateObject(estadoAtual, "estado", Groups.AlterarEstado.class);

		Estado estadoAtualizado = estadoService.alterar(estadoAtual);
		return GenericMapper.map(estadoAtualizado, EstadoOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive(message = "{positive}") Long estadoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		estadoService.remover(estadoId);
	}
}
