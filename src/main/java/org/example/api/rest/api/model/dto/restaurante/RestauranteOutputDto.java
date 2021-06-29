package org.example.api.rest.api.model.dto.restaurante;

import java.math.BigDecimal;

import org.example.api.rest.api.model.dto.endereco.EnderecoOutputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteOutputDto {
	private Long id;
	private String nome;
	private BigDecimal taxaFrete;
	private String cozinha;
	private Boolean ativo;
	private EnderecoOutputDto endereco;
}
