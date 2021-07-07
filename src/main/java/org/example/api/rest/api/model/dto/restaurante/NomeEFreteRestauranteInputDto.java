package org.example.api.rest.api.model.dto.restaurante;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NomeEFreteRestauranteInputDto {

	@NotBlank(message = "{notBlank}")
	private String nome;
	
	@NotNull(message = "{notNull}")
	@PositiveOrZero(message = "{positiveOrZero}")
	private BigDecimal taxaFrete;
}
