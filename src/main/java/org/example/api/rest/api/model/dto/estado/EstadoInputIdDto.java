package org.example.api.rest.api.model.dto.estado;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoInputIdDto {
	@NotNull
	private Long id;
}
