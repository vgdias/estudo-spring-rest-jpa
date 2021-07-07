package org.example.api.rest.api.model.dto.estado;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstadoInputDto {
	@NotBlank(message = "{notBlank}")
	private String nome;
}
