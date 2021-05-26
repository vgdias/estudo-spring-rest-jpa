package org.example.api.rest.api.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.cidade.CidadeInputDto;
import org.example.api.rest.api.model.dto.cidade.CidadeOutputDto;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.service.CadastroCidadeService;
import org.example.api.rest.shared.mapper.GenericMapper;
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
@RequestMapping("/cidades")
public class CidadeController {

	@Autowired
	private CadastroCidadeService cadastroCidadeService;

	@GetMapping()
	public List<CidadeOutputDto> todas() {
		List<Cidade> cidades = cadastroCidadeService.listar();
		return GenericMapper.collectionMap(cidades, CidadeOutputDto.class);
	}

	@GetMapping("/{id}")
	public CidadeOutputDto buscar(@PathVariable("id") @Positive Long cidadeId) {
		Cidade cidade = cadastroCidadeService.buscar(cidadeId);
		return GenericMapper.map(cidade, CidadeOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeOutputDto adicionar(@RequestBody @Valid CidadeInputDto cidadeNova) {
		Cidade cidadeAdicionada = cadastroCidadeService.adicionar(GenericMapper.map(cidadeNova, Cidade.class));
		return GenericMapper.map(cidadeAdicionada, CidadeOutputDto.class);
	}

	@PutMapping("/{id}")
	public CidadeOutputDto alterar(@PathVariable("id") @Positive Long cidadeAtualId, 
			@RequestBody Map<String, Object> propriedadesCidadeNova, HttpServletRequest request) {

		Cidade cidadeAtualizada = cadastroCidadeService.alterar(propriedadesCidadeNova, cidadeAtualId, request);
		return GenericMapper.map(cidadeAtualizada, CidadeOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") Long cidadeId) {
		cadastroCidadeService.remover(cidadeId);
	}
}
