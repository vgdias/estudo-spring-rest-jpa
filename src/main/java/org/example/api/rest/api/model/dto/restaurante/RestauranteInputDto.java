package org.example.api.rest.api.model.dto.restaurante;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.example.api.rest.api.model.dto.cozinha.CozinhaIdInputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteInputDto {

	@NotBlank
	private String nome;

	@NotNull
	@PositiveOrZero
	private BigDecimal taxaFrete;

	@Valid
	@NotNull
	private CozinhaIdInputDto cozinha;
}
