package org.example.api.rest.domain.repository;

import java.util.List;
import java.util.Optional;

import org.example.api.rest.domain.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PedidoRepository extends 
JpaRepository<Pedido, Long>,
JpaSpecificationExecutor<Pedido> {

	Optional<Pedido> findByCodigo(String codigo);
	
	@Query("from Pedido p join fetch p.cliente join fetch p.restaurante r join fetch r.cozinha")
	List<Pedido> findAll();
}