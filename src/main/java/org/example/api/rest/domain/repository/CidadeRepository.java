package org.example.api.rest.domain.repository;

import java.util.List;
import java.util.Optional;

import org.example.api.rest.domain.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CidadeRepository extends 
JpaRepository<Cidade, Long>,
JpaSpecificationExecutor<Cidade> {

	// Resolvendo o problema do N + 1
	@Query("from Cidade c join c.estado")
	List<Cidade> findAll();

	Optional<Cidade> findByNomeAndEstadoId(String nome, Long id);
}
