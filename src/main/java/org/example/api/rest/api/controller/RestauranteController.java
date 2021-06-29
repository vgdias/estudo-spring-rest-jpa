package org.example.api.rest.api.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.example.api.rest.api.model.dto.endereco.EnderecoInputDto;
import org.example.api.rest.api.model.dto.formapagamento.FormaPagamentoOutputDto;
import org.example.api.rest.api.model.dto.restaurante.NomeEFreteRestauranteInputDto;
import org.example.api.rest.api.model.dto.restaurante.RestauranteInputDto;
import org.example.api.rest.api.model.dto.restaurante.RestauranteOutputDto;
import org.example.api.rest.domain.model.Endereco;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.service.CadastroRestauranteService;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.example.api.rest.shared.validation.GenericValidator;
import org.example.api.rest.shared.validation.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

	@Autowired
	private CadastroRestauranteService cadastroRestauranteService;

	@GetMapping
	public List<RestauranteOutputDto> listar() {
		List<Restaurante> restaurantes = cadastroRestauranteService.listar();
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/{id}/formas-pagamento")
	public List<FormaPagamentoOutputDto> listarFormasPagamentoRestaurante(@PathVariable("id") @Positive Long restauranteId) {
		Restaurante restaurante = cadastroRestauranteService.buscar(restauranteId);
		return GenericMapper.collectionMap(restaurante.getFormasPagamento(), FormaPagamentoOutputDto.class);
	}

	@GetMapping("/{id}")
	public RestauranteOutputDto buscar(@PathVariable("id") @Positive Long restauranteId) {
		Restaurante restaurante = cadastroRestauranteService.buscar(restauranteId);
		return GenericMapper.map(restaurante, RestauranteOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteOutputDto adicionar(@Valid @RequestBody RestauranteInputDto restauranteNovoDto) {
		Restaurante restauranteNovo = GenericMapper.map(restauranteNovoDto, Restaurante.class);
		Restaurante restauranteAdicionado = cadastroRestauranteService.adicionar(restauranteNovo);
		return GenericMapper.map(restauranteAdicionado, RestauranteOutputDto.class);
	}

	@PatchMapping("/{id}")
	public RestauranteOutputDto alterar(@PathVariable("id") @Positive Long restauranteAtualId,
			@RequestBody Map<String, Object> propriedadesRestauranteNovo, 
			HttpServletRequest request) {

		List<String> propriedadesNaoPermitidas = Arrays.asList(
				"id", "cozinha", "endereco", "dataCadastro", 
				"dataAtualizacao", "formasPagamento", "produtos");

		GenericValidator.validateProperties(propriedadesRestauranteNovo, propriedadesNaoPermitidas);
		Restaurante restauranteAtual = cadastroRestauranteService.buscar(restauranteAtualId);
		GenericMapper.map(propriedadesRestauranteNovo, restauranteAtual, Restaurante.class, request);
		GenericValidator.validateObject(restauranteAtual, "restaurante", Groups.AlterarRestaurante.class);

		Restaurante restauranteAtualizado = cadastroRestauranteService.alterar(restauranteAtual);
		return GenericMapper.map(restauranteAtualizado, RestauranteOutputDto.class);
	}

	@PatchMapping("/{id}/alterar-nome-e-frete")
	public RestauranteOutputDto alterarNomeEFrete(@PathVariable("id") @Positive Long restauranteAtualId, 
			@Valid @RequestBody NomeEFreteRestauranteInputDto nomeEFreteRestauranteNovoDto) {

		Restaurante nomeEFreteRestauranteNovo = GenericMapper.map(nomeEFreteRestauranteNovoDto, Restaurante.class);
		Restaurante restauranteAtualizado = cadastroRestauranteService.alterarNomeEFrete(nomeEFreteRestauranteNovo, restauranteAtualId);
		return GenericMapper.map(restauranteAtualizado, RestauranteOutputDto.class);
	}

	@PatchMapping("/{id}/alterar-endereco")
	public RestauranteOutputDto alterarEnderecoDeRestaurante(@PathVariable("id") @Positive Long restauranteAtualId, 
			@Valid @RequestBody EnderecoInputDto enderecoNovoDto) {

		Endereco enderecoNovo = GenericMapper.map(enderecoNovoDto, Endereco.class);
		Restaurante restauranteAtualizado = cadastroRestauranteService.alterarEnderecoDeRestaurante(enderecoNovo, restauranteAtualId);
		return GenericMapper.map(restauranteAtualizado, RestauranteOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive Long restauranteId) {
		cadastroRestauranteService.remover(restauranteId);
	}

	@PutMapping("/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativar(@PathVariable("id") Long restauranteId) {
		cadastroRestauranteService.ativar(restauranteId);
	}

	@DeleteMapping("/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativar(@PathVariable("id") Long restauranteId) {
		cadastroRestauranteService.inativar(restauranteId);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante")
	public List<RestauranteOutputDto> comFreteGratisENomeSemelhante(@RequestParam @NotBlank String nome,
			HttpServletRequest request) 
					throws MissingServletRequestParameterException {

		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList("nome")); 
		List<Restaurante> restaurantes = cadastroRestauranteService
				.restauranteComFreteGratisComNomeSemelhante(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/por-intervalo-de-taxa-frete")
	public List<RestauranteOutputDto> porIntervaloDeTaxaFrete(@RequestParam @PositiveOrZero BigDecimal taxaInicial,
			@RequestParam @PositiveOrZero BigDecimal taxaFinal, HttpServletRequest request) {

		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList("taxaInicial", "taxaFinal")); 
		List<Restaurante> restaurantes = cadastroRestauranteService.restaurantePorIntervaloDeTaxaFrete(taxaInicial, taxaFinal);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class); 
	}

	@GetMapping("/quantos-por-cozinhaId")
	public int quantosPorCozinhaId(@RequestParam @Positive Long cozinhaId, HttpServletRequest request) {
		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList("cozinhaId")); 
		return cadastroRestauranteService.quantosRestaurantesPorCozinhaId(cozinhaId);
	}

	@GetMapping("/quantos-por-cozinhaNome")
	public int quantosPorCozinhaNome(@RequestParam @NotBlank String cozinhaNome, HttpServletRequest request) {
		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList("cozinhaNome")); 
		return cadastroRestauranteService.quantosRestaurantesPorCozinhaNome(cozinhaNome);
	}

	@GetMapping("/com-nome-semelhante-e-cozinhaId")
	public List<RestauranteOutputDto> comNomeSemelhanteECozinhaId(@RequestParam @NotBlank String nome, 
			@RequestParam @Positive Long cozinhaId, HttpServletRequest request) {

		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList("nome", "cozinhaId")); 
		List<Restaurante> restaurantes = cadastroRestauranteService.
				restauranteComNomeSemelhanteECozinhaId(nome, cozinhaId);

		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-nome-semelhante")
	public List<RestauranteOutputDto> comNomeSemelhante(@RequestParam @NotBlank String nome, HttpServletRequest request) {
		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList("nome")); 
		List<Restaurante> restaurantes =  cadastroRestauranteService.restauranteComNomeSemelhante(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-customizada-por-nome-e-frete")
	public List<RestauranteOutputDto> buscaCustomizadaPorNomeEFrete(@RequestParam @NotBlank String nome, 
			@RequestParam @PositiveOrZero BigDecimal taxaFreteInicial, 
			@RequestParam("taxaFreteFinal") @PositiveOrZero  BigDecimal taxaFinal, // renomeando parametro recebido 
			HttpServletRequest request) {

		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList("taxaFreteInicial", "taxaFinal")); 
		List<Restaurante> restaurantes = cadastroRestauranteService.buscaCustomizadaPorNomeEFrete(
				nome, taxaFreteInicial, taxaFinal);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-dinamica")
	public List<RestauranteOutputDto> buscaDinamica(@RequestParam @NotBlank String nome, 
			@RequestParam @PositiveOrZero BigDecimal taxaFreteInicial, 
			@RequestParam @PositiveOrZero BigDecimal taxaFreteFinal, 
			@RequestParam @NotBlank String nomeCozinha, HttpServletRequest request) {

		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList(
				"nome", "taxaFreteInicial", "taxaFreteFinal", "nomeCozinha")); 
		List<Restaurante> restaurantes = cadastroRestauranteService.buscaDinamica(
				nome, taxaFreteInicial, taxaFreteFinal, nomeCozinha);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante-spec")
	public List<RestauranteOutputDto> comFreteGratisENomeSemelhanteSpec(@RequestParam @NotBlank String nome,
			HttpServletRequest request) {

		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList("nome")); 
		List<Restaurante> restaurantes = cadastroRestauranteService.restaurantesComFreteGratisENomeSemelhanteSpec(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante-spec2")
	public List<RestauranteOutputDto> comFreteGratisENomeSemelhanteSpec2(@RequestParam @NotBlank String nome,
			HttpServletRequest request) {

		GenericValidator.validateParameters( request.getParameterNames(), Arrays.asList("nome")); 
		List<Restaurante> restaurantes = cadastroRestauranteService.restaurantesComFreteGratisENomeSemelhanteSpec2(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

}