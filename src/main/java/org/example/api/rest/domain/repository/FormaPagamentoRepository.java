package org.example.api.rest.domain.repository;

import java.util.Optional;

import org.example.api.rest.domain.model.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long>,
JpaSpecificationExecutor<FormaPagamento> {

	Optional<FormaPagamento> findByDescricao(String descricao);
}
