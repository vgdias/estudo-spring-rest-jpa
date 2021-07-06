package org.example.api.rest.domain.repository;

import java.util.Optional;

import org.example.api.rest.domain.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GrupoRepository extends 
JpaRepository<Grupo, Long>,
JpaSpecificationExecutor<Grupo> {

	Optional<Grupo> findByNome(String nome);

}
