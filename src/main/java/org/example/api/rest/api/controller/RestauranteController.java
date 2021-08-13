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
import org.example.api.rest.api.model.dto.restaurante.NomeFreteRestauranteInputDto;
import org.example.api.rest.api.model.dto.restaurante.RestauranteInputDto;
import org.example.api.rest.api.model.dto.restaurante.RestauranteOutputDto;
import org.example.api.rest.domain.model.Endereco;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.service.RestauranteService;
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
	private RestauranteService restauranteService;

	@GetMapping
	public List<RestauranteOutputDto> listar(HttpServletRequest request) {
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<Restaurante> restaurantes = restauranteService.listar();
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/{id}")
	public RestauranteOutputDto buscarPorId(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList()); 
		Restaurante restaurante = restauranteService.buscarPorId(restauranteId);
		return GenericMapper.map(restaurante, RestauranteOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RestauranteOutputDto adicionar(
			@Valid @RequestBody RestauranteInputDto restauranteNovoDto,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList()); 
		Restaurante restauranteNovo = GenericMapper.map(restauranteNovoDto, Restaurante.class);
		Restaurante restauranteAdicionado = restauranteService.adicionar(restauranteNovo);
		return GenericMapper.map(restauranteAdicionado, RestauranteOutputDto.class);
	}

	@PatchMapping("/{id}")
	public RestauranteOutputDto alterar(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId, 
			@RequestBody Map<String, Object> propriedadesRestauranteNovo, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<String> propriedadesNaoPermitidas = Arrays.asList(
				"id", "cozinha", "endereco", "dataCadastro", 
				"dataAtualizacao", "formasPagamento", "produtos", "ativo");
		GenericValidator.validateProperties(propriedadesRestauranteNovo, propriedadesNaoPermitidas);

		Restaurante restauranteAtual = restauranteService.buscarPorId(restauranteId);
		GenericMapper.map(propriedadesRestauranteNovo, restauranteAtual, Restaurante.class, request);
		GenericValidator.validateObject(restauranteAtual, "restaurante", Groups.AlterarRestaurante.class);

		Restaurante restauranteAtualizado = restauranteService.alterar(restauranteAtual);
		return GenericMapper.map(restauranteAtualizado, RestauranteOutputDto.class);
	}

	@PatchMapping("/{id}/alterar-nome-e-frete")
	public RestauranteOutputDto alterarNomeEFrete(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId, 
			@Valid @RequestBody NomeFreteRestauranteInputDto nomeEFreteRestauranteNovoDto,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Restaurante nomeEFreteRestauranteNovo = GenericMapper.map(nomeEFreteRestauranteNovoDto, Restaurante.class);
		Restaurante restauranteAtualizado = restauranteService.alterarNomeEFrete(nomeEFreteRestauranteNovo, restauranteId);
		return GenericMapper.map(restauranteAtualizado, RestauranteOutputDto.class);
	}

	@PatchMapping("/{id}/alterar-endereco")
	public RestauranteOutputDto alterarEnderecoDeRestaurante(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteAtualId, 
			@Valid @RequestBody EnderecoInputDto enderecoNovoDto, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Endereco enderecoNovo = GenericMapper.map(enderecoNovoDto, Endereco.class);
		Restaurante restauranteAtualizado = restauranteService.alterarRestauranteEndereco(enderecoNovo, restauranteAtualId);
		return GenericMapper.map(restauranteAtualizado, RestauranteOutputDto.class);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		restauranteService.remover(restauranteId);
	}

	@PutMapping("/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void ativar(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		restauranteService.ativar(restauranteId);
	}

	@DeleteMapping("/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void inativar(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		restauranteService.inativar(restauranteId);
	}

	@PutMapping("/{id}/abertura")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void abrir(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		restauranteService.abrir(restauranteId);
	}

	@PutMapping("/{id}/fechamento")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void fechar(
			@PathVariable("id") @Positive(message = "{positive}") Long restauranteId, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		restauranteService.fechar(restauranteId);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante")
	public List<RestauranteOutputDto> comFreteGratisENomeSemelhante(
			@RequestParam @NotBlank(message = "{notBlank}") String nome,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList("nome")); 
		List<Restaurante> restaurantes = restauranteService
				.restauranteComFreteGratisComNomeSemelhante(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/por-intervalo-de-taxa-frete")
	public List<RestauranteOutputDto> porIntervaloDeTaxaFrete(
			@RequestParam @PositiveOrZero(message = "{positiveOrZero}") BigDecimal taxaInicial,
			@RequestParam @PositiveOrZero(message = "{positiveOrZero}") BigDecimal taxaFinal, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams( request.getParameterNames(), Arrays.asList("taxaInicial", "taxaFinal")); 
		List<Restaurante> restaurantes = restauranteService.restaurantePorIntervaloDeTaxaFrete(taxaInicial, taxaFinal);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class); 
	}

	@GetMapping("/quantos-por-cozinhaId")
	public int quantosPorCozinhaId(
			@RequestParam @Positive(message = "{positive}") Long cozinhaId, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams( request.getParameterNames(), Arrays.asList("cozinhaId")); 
		return restauranteService.quantosRestaurantesPorCozinhaId(cozinhaId);
	}

	@GetMapping("/quantos-por-cozinhaNome")
	public int quantosPorCozinhaNome(
			@RequestParam @NotBlank(message = "{notBlank}") String cozinhaNome, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams( request.getParameterNames(), Arrays.asList("cozinhaNome")); 
		return restauranteService.quantosRestaurantesPorCozinhaNome(cozinhaNome);
	}

	@GetMapping("/com-nome-semelhante-e-cozinhaId")
	public List<RestauranteOutputDto> comNomeSemelhanteECozinhaId(
			@RequestParam @NotBlank(message = "{notBlank}") String nome, 
			@RequestParam @Positive(message = "{positive}") Long cozinhaId, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams( request.getParameterNames(), Arrays.asList("nome", "cozinhaId")); 
		List<Restaurante> restaurantes = restauranteService.
				restauranteComNomeSemelhanteECozinhaId(nome, cozinhaId);

		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-nome-semelhante")
	public List<RestauranteOutputDto> comNomeSemelhante(
			@RequestParam @NotBlank(message = "{notBlank}") String nome, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams( request.getParameterNames(), Arrays.asList("nome")); 
		List<Restaurante> restaurantes =  restauranteService.restauranteComNomeSemelhante(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-customizada-por-nome-e-frete")
	public List<RestauranteOutputDto> buscaCustomizadaPorNomeEFrete(
			@RequestParam @NotBlank(message = "{notBlank}") String nome, 
			@RequestParam @PositiveOrZero(message = "{positiveOrZero}") BigDecimal taxaFreteInicial, 
			@RequestParam("taxaFreteFinal") @PositiveOrZero(message = "{positiveOrZero}")  BigDecimal taxaFinal,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams( request.getParameterNames(), Arrays.asList("nome", "taxaFreteInicial", "taxaFinal")); 
		List<Restaurante> restaurantes = restauranteService.buscaCustomizadaPorNomeEFrete(
				nome, taxaFreteInicial, taxaFinal);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/busca-dinamica")
	public List<RestauranteOutputDto> buscaDinamica(
			@RequestParam @NotBlank(message = "{notBlank}") String nome, 
			@RequestParam @PositiveOrZero(message = "{positiveOrZero}") BigDecimal taxaFreteInicial, 
			@RequestParam @PositiveOrZero(message = "{positiveOrZero}") BigDecimal taxaFreteFinal, 
			@RequestParam @NotBlank(message = "{notBlank}") String nomeCozinha, 
			HttpServletRequest request) {

		GenericValidator.validateRequestParams( request.getParameterNames(), Arrays.asList(
				"nome", "taxaFreteInicial", "taxaFreteFinal", "nomeCozinha")); 
		List<Restaurante> restaurantes = restauranteService.buscaDinamica(
				nome, taxaFreteInicial, taxaFreteFinal, nomeCozinha);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante-spec")
	public List<RestauranteOutputDto> comFreteGratisENomeSemelhanteSpec(
			@RequestParam @NotBlank(message = "{notBlank}") String nome,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams( request.getParameterNames(), Arrays.asList("nome")); 
		List<Restaurante> restaurantes = restauranteService.restaurantesComFreteGratisENomeSemelhanteSpec(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

	@GetMapping("/com-frete-gratis-e-nome-semelhante-spec2")
	public List<RestauranteOutputDto> comFreteGratisENomeSemelhanteSpec2(
			@RequestParam @NotBlank(message = "{notBlank}") String nome,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams( request.getParameterNames(), Arrays.asList("nome")); 
		List<Restaurante> restaurantes = restauranteService.restaurantesComFreteGratisENomeSemelhanteSpec2(nome);
		return GenericMapper.collectionMap(restaurantes, RestauranteOutputDto.class);
	}

}