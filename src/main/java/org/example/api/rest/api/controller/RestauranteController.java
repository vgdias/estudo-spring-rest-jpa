package org.example.api.rest.api.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.example.api.rest.api.model.dto.restaurante.RestauranteInputDto;
import org.example.api.rest.api.model.dto.restaurante.RestauranteOutputDto;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.service.CadastroRestauranteService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/{restauranteId}")
	public RestauranteOutputDto buscar(@PathVariable("restauranteId") Long id) {
		Restaurante restaurante = cadastroRestauranteService.buscar(id);
		return GenericMapper.map(restaurante, RestauranteOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteOutputDto adicionar(@RequestBody RestauranteInputDto restauranteNovo) {
		Restaurante restauranteAdicionado = cadastroRestauranteService.adicionar(GenericMapper.map(restauranteNovo, Restaurante.class));
		return GenericMapper.map(restauranteAdicionado, RestauranteOutputDto.class);
	}

	@PutMapping("/{restauranteId}")
	public RestauranteOutputDto alterar(@PathVariable("restauranteId")  Long restauranteAtualId, 
			@RequestBody Map<String, Object> propriedadesRestauranteNovo, HttpServletRequest request) {

		Restaurante restauranteAtualizado = cadastroRestauranteService.alterar(propriedadesRestauranteNovo, restauranteAtualId, request);
		return GenericMapper.map(restauranteAtualizado, RestauranteOutputDto.class);
	}

	@DeleteMapping("/{restauranteId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long restauranteId) {
		cadastroRestauranteService.remover(restauranteId);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante")
	public List<RestauranteOutputDto> restauranteComFreteGratis(@RequestParam String nome) {
		List<Restaurante> restaurantes = cadastroRestauranteService.restauranteComFreteGratisComNomeSemelhante(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/por-intervalo-de-taxa-frete")
	public List<RestauranteOutputDto> restaurantePorIntervaloDeTaxaFrete(BigDecimal taxaInicial, BigDecimal taxaFinal) {
		List<Restaurante> restaurantes = cadastroRestauranteService.restaurantePorIntervaloDeTaxaFrete(taxaInicial, taxaFinal);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class); 
	}

	@GetMapping("/quantos-por-cozinhaId")
	public int quantosRestaurantesPorCozinhaId(Long cozinhaId) {
		return cadastroRestauranteService.quantosRestaurantesPorCozinhaId(cozinhaId);
	}

	@GetMapping("/quantos-por-cozinhaNome")
	public int quantosRestaurantesPorCozinhaNome(String cozinhaNome) {
		return cadastroRestauranteService.quantosRestaurantesPorCozinhaNome(cozinhaNome);
	}

	@GetMapping("/com-nome-semelhante-e-cozinhaId")
	public List<RestauranteOutputDto> restaurantesComNomeSemelhanteECozinhaId(String nome, Long cozinhaId) {
		List<Restaurante> restaurantes = cadastroRestauranteService.
				restauranteComNomeSemelhanteECozinhaId(nome, cozinhaId);

		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-nome-semelhante")
	public List<RestauranteOutputDto> restaurantesComNomeSemelhante(String nome) {
		List<Restaurante> restaurantes =  cadastroRestauranteService.restauranteComNomeSemelhante(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-customizada-por-nome-e-frete")
	public List<RestauranteOutputDto> buscaCustomizadaPorNomeEFrete(String nome, BigDecimal taxaFreteInicial, 
			@RequestParam("taxaFreteFinal") BigDecimal taxaFinal) {// renomeando parametro recebido

		List<Restaurante> restaurantes = cadastroRestauranteService.buscaCustomizadaPorNomeEFrete(nome, taxaFreteInicial, taxaFinal);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-dinamica")
	public List<RestauranteOutputDto> buscaDinamica(String nome, BigDecimal taxaFreteInicial, 
			BigDecimal taxaFreteFinal, String nomeCozinha) {

		List<Restaurante> restaurantes = cadastroRestauranteService.buscaDinamica(nome, taxaFreteInicial, taxaFreteFinal, nomeCozinha);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-specification")
	public List<RestauranteOutputDto> restaurantesComFreteGratisENomeSemelhanteSpec(String nome) {
		List<Restaurante> restaurantes = cadastroRestauranteService.restaurantesComFreteGratisENomeSemelhanteSpec(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-specification2")
	public List<RestauranteOutputDto> restaurantesComFreteGratisENomeSemelhanteSpec2(String nome) {
		List<Restaurante> restaurantes = cadastroRestauranteService.restaurantesComFreteGratisENomeSemelhanteSpec2(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

}