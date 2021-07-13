package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.example.api.rest.api.model.dto.produto.ProdutoInputDto;
import org.example.api.rest.api.model.dto.produto.ProdutoOutputDto;
import org.example.api.rest.domain.model.Produto;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.service.RestauranteService;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.example.api.rest.shared.validation.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/restaurantes/{restauranteId}/produtos")
public class RestauranteProdutoController {

	@Autowired
	private RestauranteService restauranteService;

	@GetMapping
	public List<ProdutoOutputDto> listarRestauranteProdutos(
			@PathVariable @Positive(message = "{positive}") Long restauranteId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Restaurante restaurante = restauranteService.buscarPorId(restauranteId);
		Set<Produto> produtos = restauranteService.listarRestauranteProdutos(restaurante);
		return GenericMapper.collectionMap(produtos, ProdutoOutputDto.class);
	}

	@GetMapping("/{produtoId}")
	public ProdutoOutputDto buscarRestauranteProdutoPorId(
			@PathVariable @Positive(message = "{positive}") Long restauranteId,
			@PathVariable @Positive(message = "{positive}") Long produtoId,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Produto produto = restauranteService.buscarRestauranteProdutoPorId(restauranteId, produtoId);
		return GenericMapper.map(produto, ProdutoOutputDto.class);
	}

	@PutMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProdutoOutputDto adicionarRestauranteProduto(
			@PathVariable @Positive(message = "{positive}") Long restauranteId, 
			@Valid @RequestBody ProdutoInputDto produtoNovoDto,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		Produto produtoNovo = GenericMapper.map(produtoNovoDto, Produto.class);
		Produto produtoInserido = restauranteService.adicionarRestauranteProduto(restauranteId, produtoNovo);
		return GenericMapper.map(produtoInserido, ProdutoOutputDto.class);
	}
}