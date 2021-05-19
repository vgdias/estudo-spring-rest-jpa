package org.example.api.rest.domain.repository;

import java.math.BigDecimal;
import java.util.List;

import org.example.api.rest.domain.model.Restaurante;
import org.springframework.stereotype.Repository;

@Repository
public interface RestauranteCustomRepository {

	List<Restaurante> buscaCustomizadaPorNomeEFrete(String nome, BigDecimal taxaFreteInicial,
			BigDecimal taxaFreteFinal);
	
	List<Restaurante> buscaDinamica(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal, 
			String nomeCozinha);

}