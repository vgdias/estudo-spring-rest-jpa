package org.example.api.rest.api.controller;

import java.util.List;
import java.util.Map;

import org.example.api.rest.api.model.dto.cidade.CidadeInputDto;
import org.example.api.rest.api.model.dto.cidade.CidadeOutputDto;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.service.CadastroCidadeService;
import org.example.api.rest.shared.mapper.GenericMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/{cidadeId}")
	public CidadeOutputDto buscar(@PathVariable("cidadeId") Long id) {
		Cidade cidade = cadastroCidadeService.buscar(id);
		return GenericMapper.map(cidade, CidadeOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CidadeOutputDto adicionar(@RequestBody CidadeInputDto cidadeNova) {
		Cidade cidadeAdicionada = cadastroCidadeService.adicionar(GenericMapper.map(cidadeNova, Cidade.class));
		return GenericMapper.map(cidadeAdicionada, CidadeOutputDto.class);
	}

	@PutMapping("/{cidadeId}")
	public CidadeOutputDto alterar(@PathVariable("cidadeId") Long cidadeAtualId, 
			@RequestBody Map<String, Object> propriedadesCidadeNova) {

		Cidade cidadeAtualizada = cadastroCidadeService.alterar(propriedadesCidadeNova, cidadeAtualId);
		return GenericMapper.map(cidadeAtualizada, CidadeOutputDto.class);
	}

	@DeleteMapping("/{cidadeId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long cidadeId) {
		cadastroCidadeService.remover(cidadeId);
	}
}
