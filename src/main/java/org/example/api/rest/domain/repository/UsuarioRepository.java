package org.example.api.rest.domain.repository;

import java.util.List;

import org.example.api.rest.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends 
JpaRepository<Usuario, Long>,
JpaSpecificationExecutor<Usuario> {

	// Resolvendo o problema do N + 1
	List<Usuario> findAll();

}