package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.RESTAURANTE_PRODUTO_POR_ID_NAO_ENCONTRADO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.RESTAURANTE_PRODUTO_POR_NOME_ENCONTRADO;

import java.util.Set;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Produto;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.repository.ProdutoRepository;
import org.example.api.rest.domain.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestauranteProdutoService {

	@Autowired 
	ProdutoRepository produtoRepository;

	@Autowired
	RestauranteRepository restauranteRepository;

	@Autowired
	ProdutoService produtoService;

	@Autowired
	RestauranteService restauranteService;

	public Set<Produto> listarRestauranteProdutos(Restaurante restaurante) {
		return produtoRepository.findByRestaurante(restaurante);
	}

	public Produto buscarRestauranteProdutoPorId(Long restauranteId, Long produtoId) {
		return produtoRepository.obterProdutoDeRestaurante(restauranteId, produtoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								RESTAURANTE_PRODUTO_POR_ID_NAO_ENCONTRADO.toString(), 
								produtoId)));
	}

	@Transactional
	public Produto adicionarRestauranteProduto(Long restauranteId, Produto produto) {
		Restaurante restaurante = restauranteService.buscarRestaurantePorId(restauranteId);
		if (verificarSeRestauranteProdutoNomeExiste(produto.getNome())) {
			throw new RecursoEmUsoException(
					String.format(
							RESTAURANTE_PRODUTO_POR_NOME_ENCONTRADO.toString(), 
							produto.getNome())
					);
		}

		produto.setRestaurante(restaurante);
		return produtoService.adicionar(produto);
	}

	private Boolean verificarSeRestauranteProdutoNomeExiste(String nome) {
		return restauranteRepository.existsByProdutosNome(nome);
	}
}