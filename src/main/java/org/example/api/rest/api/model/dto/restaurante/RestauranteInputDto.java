package org.example.api.rest.api.model.dto.restaurante;

import java.math.BigDecimal;

import org.example.api.rest.api.model.dto.cozinha.CozinhaIdInputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteInputDto {
	private String nome;
	private BigDecimal taxaFrete;
	private CozinhaIdInputDto cozinha;
}
