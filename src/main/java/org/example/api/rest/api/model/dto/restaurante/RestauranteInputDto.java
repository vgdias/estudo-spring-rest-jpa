package org.example.api.rest.api.model.dto.restaurante;

import java.math.BigDecimal;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.example.api.rest.api.model.dto.cozinha.IdCozinhaInputDto;
import org.example.api.rest.api.model.dto.endereco.EnderecoInputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteInputDto {

	@NotBlank(message = "{notBlank}")
	private String nome;

	@NotNull(message = "{notNull}")
	@PositiveOrZero(message = "{positiveOrZero}")
	private BigDecimal taxaFrete;

	@Valid
	@NotNull(message = "{notNull}")
	private IdCozinhaInputDto cozinha;

	@Valid
	@NotNull(message = "{notNull}")
	private EnderecoInputDto endereco;

}