package org.example.api.rest.api.model.dto.usuario;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdUsuarioInputDto {
	@Positive(message = "{positive}")
	@NotNull(message = "{notNull}")
	private Long id;
}
