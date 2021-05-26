package org.example.api.rest.api.model.dto.restaurante;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestauranteIdInputDto {
	@NotNull
	private Long id;
}
