package org.example.api.rest.domain.repository;

import org.example.api.rest.domain.model.Permissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PermissaoRepository extends JpaRepository<Permissao, Long>,
JpaSpecificationExecutor<Permissao> {
}