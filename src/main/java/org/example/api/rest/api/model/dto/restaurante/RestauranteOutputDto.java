package org.example.api.rest.api.model.dto.restaurante;

import java.math.BigDecimal;

import org.example.api.rest.api.model.dto.cozinha.CozinhaOutputDto;
import org.example.api.rest.api.model.dto.endereco.EnderecoOutputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteOutputDto {
	private Long id;
	private String nomeRestaurante;
	private BigDecimal taxaFreteRestaurante;
	private CozinhaOutputDto cozinha;
	private Boolean ativo;
	private EnderecoOutputDto endereco;
}
