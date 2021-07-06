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
import org.example.api.rest.domain.service.CadastroCidadeService;
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
	private CadastroCidadeService cadastroCidadeService;

	@GetMapping()
	public List<CidadeOutputDto> listar() {
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
	public CidadeOutputDto adicionar(@Valid @RequestBody CidadeInputDto cidadeNovaDto) {
		Cidade cidadeNova = GenericMapper.map(cidadeNovaDto, Cidade.class);
		Cidade cidadeAdicionada = cadastroCidadeService.adicionar(cidadeNova);
		return GenericMapper.map(cidadeAdicionada, CidadeOutputDto.class);
	}

	@PatchMapping("/{id}")
	public CidadeOutputDto alterar(@PathVariable("id") @Positive Long cidadeAtualId, 
			@RequestBody Map<String, Object> propriedadesCidadeNova, 
			HttpServletRequest request) {

		List<String> propriedadesNaoPermitidas = Arrays.asList("id");
		GenericValidator.validateProperties(propriedadesCidadeNova, propriedadesNaoPermitidas);

		Cidade cidadeAtual = cadastroCidadeService.obterCidadePorId(cidadeAtualId);
		GenericMapper.map(propriedadesCidadeNova, cidadeAtual, Cidade.class, request);
		GenericValidator.validateObject(cidadeAtual, "cidade", Groups.AlterarCidade.class);

		Cidade cidadeAtualizado = cadastroCidadeService.alterar(cidadeAtual);
		return GenericMapper.map(cidadeAtualizado, CidadeOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") Long cidadeId) {
		cadastroCidadeService.remover(cidadeId);
	}
}
