package org.example.api.rest.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
	@Query("from Restaurante r left join r.cozinha left join r.endereco.cidade c left join c.estado where r.nome = ?1")
	Optional<Restaurante> findByNome(String nome);

	// Resolvendo o problema do N + 1
	@Query("from Restaurante r left join r.cozinha left join r.endereco.cidade c left join c.estado")
	List<Restaurante> findAll();

	// Resolvendo o problema do N + 1
	@Query("from Restaurante r left join r.cozinha left join r.endereco.cidade c left join c.estado where r.id = ?1")
	Optional<Restaurante> findById(Long id);
	
	Boolean existsByProdutosNome(String nome);

}
