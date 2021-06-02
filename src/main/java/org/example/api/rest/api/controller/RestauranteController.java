package org.example.api.rest.api.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.example.api.rest.api.model.dto.restaurante.NomeEFreteRestauranteInputDto;
import org.example.api.rest.api.model.dto.restaurante.RestauranteInputDto;
import org.example.api.rest.api.model.dto.restaurante.RestauranteOutputDto;
import org.example.api.rest.domain.exception.ValidacaoException;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.service.CadastroRestauranteService;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.example.api.rest.shared.validation.Groups;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.annotation.Validated;
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

	@Autowired
	private SmartValidator validator;

	@GetMapping
	public List<RestauranteOutputDto> listar() {
		List<Restaurante> restaurantes = cadastroRestauranteService.listar();
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/{id}")
	public RestauranteOutputDto buscar(@PathVariable("id") @Positive Long restauranteId) {
		Restaurante restaurante = cadastroRestauranteService.buscar(restauranteId);
		return GenericMapper.map(restaurante, RestauranteOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteOutputDto adicionar(@Valid @RequestBody RestauranteInputDto restauranteNovo) {
		Restaurante restauranteAdicionado = cadastroRestauranteService.adicionar(
				GenericMapper.map(restauranteNovo, Restaurante.class));

		return GenericMapper.map(restauranteAdicionado, RestauranteOutputDto.class);
	}

	@PutMapping("/alterar/{id}")
	public RestauranteOutputDto alterar(@PathVariable("id") @Positive Long restauranteAtualId,
			@RequestBody Map<String, Object> propriedadesRestauranteNovo, 
			HttpServletRequest request) {

		Restaurante restauranteAtual = cadastroRestauranteService.obtemRestaurante(restauranteAtualId);
		GenericMapper.map(propriedadesRestauranteNovo, restauranteAtual, Restaurante.class, request);
		validate(restauranteAtual, "restaurante");
		Restaurante restauranteAtualizado = cadastroRestauranteService.alterar(restauranteAtual);
		return GenericMapper.map(restauranteAtualizado, RestauranteOutputDto.class);
	}

	private void validate(Restaurante restauranteAtual, String objectName) {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(restauranteAtual, objectName);
		validator.validate(restauranteAtual, bindingResult, Groups.AlterarRestaurante.class);
		if (bindingResult.hasErrors()) {
			throw new ValidacaoException(bindingResult);
		}
	}

	@PatchMapping("/alterar-nome-e-frete/{id}")
	public RestauranteOutputDto alterarNomeEFrete(@PathVariable("id") @Positive Long restauranteAtualId, 
			@Valid @RequestBody NomeEFreteRestauranteInputDto nomeEFreteRestauranteNovo) {

		Restaurante restauranteAtualizado = cadastroRestauranteService.alterarNomeEFrete(
				GenericMapper.map(nomeEFreteRestauranteNovo, Restaurante.class), 	restauranteAtualId);
		return GenericMapper.map(restauranteAtualizado, RestauranteOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable("id") @Positive Long restauranteId) {
		cadastroRestauranteService.remover(restauranteId);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante")
	public List<RestauranteOutputDto> comFreteGratisENomeSemelhante(@RequestParam @NotBlank String nome) {
		List<Restaurante> restaurantes = cadastroRestauranteService.restauranteComFreteGratisComNomeSemelhante(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/por-intervalo-de-taxa-frete")
	public List<RestauranteOutputDto> porIntervaloDeTaxaFrete(@RequestParam @PositiveOrZero BigDecimal taxaInicial,
			@RequestParam @PositiveOrZero BigDecimal taxaFinal) {
		List<Restaurante> restaurantes = cadastroRestauranteService.restaurantePorIntervaloDeTaxaFrete(taxaInicial, taxaFinal);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class); 
	}

	@GetMapping("/quantos-por-cozinhaId")
	public int quantosPorCozinhaId(@RequestParam @Positive Long cozinhaId) {
		return cadastroRestauranteService.quantosRestaurantesPorCozinhaId(cozinhaId);
	}

	@GetMapping("/quantos-por-cozinhaNome")
	public int quantosPorCozinhaNome(@RequestParam @NotBlank String cozinhaNome) {
		return cadastroRestauranteService.quantosRestaurantesPorCozinhaNome(cozinhaNome);
	}

	@GetMapping("/com-nome-semelhante-e-cozinhaId")
	public List<RestauranteOutputDto> comNomeSemelhanteECozinhaId(@RequestParam @NotBlank String nome, 
			@RequestParam @Positive Long cozinhaId) {
		List<Restaurante> restaurantes = cadastroRestauranteService.
				restauranteComNomeSemelhanteECozinhaId(nome, cozinhaId);

		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-nome-semelhante")
	public List<RestauranteOutputDto> comNomeSemelhante(@RequestParam @NotBlank String nome) {
		List<Restaurante> restaurantes =  cadastroRestauranteService.restauranteComNomeSemelhante(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-customizada-por-nome-e-frete")
	public List<RestauranteOutputDto> buscaCustomizadaPorNomeEFrete(@RequestParam @NotBlank String nome, 
			@RequestParam @PositiveOrZero BigDecimal taxaFreteInicial, 
			@RequestParam("taxaFreteFinal") @PositiveOrZero  BigDecimal taxaFinal) {// renomeando parametro recebido

		List<Restaurante> restaurantes = cadastroRestauranteService.buscaCustomizadaPorNomeEFrete(
				nome, taxaFreteInicial, taxaFinal);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-dinamica")
	public List<RestauranteOutputDto> buscaDinamica(@RequestParam @NotBlank String nomeRestaurante, 
			@RequestParam @PositiveOrZero BigDecimal taxaFreteInicial, 
			@RequestParam @PositiveOrZero BigDecimal taxaFreteFinal, 
			@RequestParam @NotBlank String nomeCozinha) {

		List<Restaurante> restaurantes = cadastroRestauranteService.buscaDinamica(
				nomeRestaurante, taxaFreteInicial, taxaFreteFinal, nomeCozinha);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante-spec")
	public List<RestauranteOutputDto> comFreteGratisENomeSemelhanteSpec(@RequestParam @NotBlank String nome) {
		List<Restaurante> restaurantes = cadastroRestauranteService.restaurantesComFreteGratisENomeSemelhanteSpec(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante-spec2")
	public List<RestauranteOutputDto> comFreteGratisENomeSemelhanteSpec2(@RequestParam @NotBlank String nome) {
		List<Restaurante> restaurantes = cadastroRestauranteService.restaurantesComFreteGratisENomeSemelhanteSpec2(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

}