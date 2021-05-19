package org.example.api.rest.domain.repository;

import java.util.List;
import java.util.Optional;

import org.example.api.rest.domain.model.Cozinha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CozinhaRepository extends 
JpaRepository<Cozinha, Long>,
JpaSpecificationExecutor<Cozinha> {

	Optional<Cozinha> findByNome(String nome);
	List<Cozinha> nomeContaining(String nome);
	
}