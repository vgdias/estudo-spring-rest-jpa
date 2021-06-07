package org.example.api.rest.api.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.estado.EstadoInputDto;
import org.example.api.rest.api.model.dto.estado.EstadoOutputDto;
import org.example.api.rest.domain.exception.ValidacaoException;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.service.CadastroEstadoService;
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
@RequestMapping("/estados")
public class EstadoController {

	@Autowired
	private CadastroEstadoService cadastroEstadoService;

	@Autowired
	private SmartValidator validator;
	
	@GetMapping
	public List<EstadoOutputDto> listar() {
		List<Estado> estados = cadastroEstadoService.listar();
		return GenericMapper.collectionMap(estados, EstadoOutputDto.class);
	}

	@GetMapping("/{id}")
	public EstadoOutputDto buscar(@PathVariable("id") @Positive Long estadoId) {
		Estado estadoAtual = cadastroEstadoService.buscar(estadoId);
		return GenericMapper.map(estadoAtual, EstadoOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoOutputDto adicionar(@Valid @RequestBody EstadoInputDto estadoNovo) {
		Estado estadoAdicionado = cadastroEstadoService.adicionar(GenericMapper.map(estadoNovo, Estado.class));
		return GenericMapper.map(estadoAdicionado, EstadoOutputDto.class);
	}

	@PutMapping("/{id}")
	public EstadoOutputDto alterar(@PathVariable("id") @Positive Long estadoAtualId, 
			@RequestBody Map<String, Object> propriedadesEstadoNovo, HttpServletRequest request) {

		this.validate(propriedadesEstadoNovo);
		Estado estadoAtual = cadastroEstadoService.obtemEstado(estadoAtualId);
		GenericMapper.map(propriedadesEstadoNovo, estadoAtual, Estado.class, request);
		this.validate(estadoAtual, "estado");

		Estado estadoAtualizado = cadastroEstadoService.alterar(estadoAtual);
		return GenericMapper.map(estadoAtualizado, EstadoOutputDto.class);
	}
	private void validate(Map<String, Object> propriedadesEstadoNovo) {
		if (propriedadesEstadoNovo.isEmpty()) {
			throw new ValidationException("Nenhum argumento fornecido");
		}
		if (propriedadesEstadoNovo.containsKey("id")) {
			throw new ValidationException("A propriedade 'id' nao pode ser alterada");
		}
	}
	private void validate(Object object, String objectName) {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, bindingResult, Groups.AlterarEstado.class);
		if (bindingResult.hasErrors()) {
			throw new ValidacaoException(bindingResult);
		}
	} 

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive Long estadoId) {
		cadastroEstadoService.remover(estadoId);
	}
}
