package org.example.api.rest.api.model.dto.restaurante;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

import org.example.api.rest.api.model.dto.cozinha.CozinhaIdInputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteInputDto {

	@NotBlank
	private String nome;
	
	@DecimalMin("1")
	private BigDecimal taxaFrete;
	
	private CozinhaIdInputDto cozinha;
}
