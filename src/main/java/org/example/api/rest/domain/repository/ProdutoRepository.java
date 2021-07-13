package org.example.api.rest.domain.repository;

import java.util.Optional;
import java.util.Set;

import org.example.api.rest.domain.model.Produto;
import org.example.api.rest.domain.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends 
JpaRepository<Produto, Long>,
JpaSpecificationExecutor<Produto> {

	@Query("from Produto where restaurante.id = :restaurante and id = :produto")
	Optional<Produto> obterProdutoDeRestaurante(@Param("restaurante") Long restauranteId, 
			@Param("produto") Long produtoId);
	
	Set<Produto> findByRestaurante(Restaurante restaurante);
	Optional<Produto> findByNome(String nome);
}
