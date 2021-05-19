package org.example.api.rest.api.model.dto.restaurante;

import java.math.BigDecimal;
import java.util.List;

import org.example.api.rest.api.model.dto.cozinha.CozinhaOutputDto;
import org.example.api.rest.domain.model.FormaPagamento;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteOutputDto {
	private Long id;
	private String nomeRestaurante;
	private BigDecimal taxaFreteRestaurante;
	private CozinhaOutputDto cozinha;
	@JsonIgnore
	private List<FormaPagamento> formasPagamento;
}
