package org.example.api.rest.api.model.dto.cidade;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.example.api.rest.api.model.dto.estado.IdEstadoInputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CidadeInputDto {
	@NotBlank(message = "{notBlank}")
	private String nome;
	
	@Valid
	@NotNull(message = "{notNull}")
	private IdEstadoInputDto estado;
}
