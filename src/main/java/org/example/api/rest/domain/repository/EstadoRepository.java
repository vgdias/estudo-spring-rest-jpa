package org.example.api.rest.domain.repository;

import org.example.api.rest.domain.model.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends 
JpaRepository<Estado, Long>,
JpaSpecificationExecutor<Estado>{}