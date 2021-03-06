package org.example.api.rest.api.model.dto.restaurante;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdRestauranteInputDto {
	@Positive(message = "{positive}")
	@NotNull(message = "{notNull}")
	private Long id;
}