package org.example.api.rest.api.model.dto.restaurante;

import java.math.BigDecimal;

import org.example.api.rest.api.model.dto.cozinha.CozinhaOutputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteOutputDto {
	private Long id;
	private String nomeRestaurante;
	private BigDecimal taxaFreteRestaurante;
	private CozinhaOutputDto cozinha;
}
