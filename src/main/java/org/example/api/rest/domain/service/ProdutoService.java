package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.PRODUTO_POR_ID_NAO_ENCONTRADO;

import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Produto;
import org.example.api.rest.domain.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;


	public Produto adicionar(Produto produto) {
		return produtoRepository.save(produto);
	}

	public Produto buscarProdutoPorId(Long produtoId) {
		return produtoRepository.findById(produtoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								PRODUTO_POR_ID_NAO_ENCONTRADO.toString(), 
								produtoId)));
	}

}