package org.example.api.rest.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import org.example.api.rest.domain.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RestauranteRepository extends 
JpaRepository<Restaurante, Long>,
RestauranteCustomRepository,
JpaSpecificationExecutor<Restaurante>{
	
	int countByCozinhaId(Long cozinhaId);
	int countByCozinhaNome(String cozinhaNome);
	List<Restaurante> findByTaxaFreteBetween(BigDecimal taxaInicial, BigDecimal taxaFinal);
	List<Restaurante> nomeContainingAndCozinhaId(String nome, Long cozinhaId);
	
	@Query("from Restaurante where nome like %:nome%")
	List<Restaurante> comNomeSemelhante(String nome);
	
	// Resolvendo o problema do N + 1
	@Query("from Restaurante r join r.cozinha left join fetch r.formasPagamento")
	List<Restaurante> findAll();
	
	// Movida para RestauranteCustomRepository
//	List<Restaurante> buscaCustomizadaPorNomeEFrete(String nome, BigDecimal taxaFreteInicial, 
//			BigDecimal taxaFreteFinal);
}
