package org.example.api.rest.api.model.dto.grupo;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrupoInputDto {

	@NotBlank(message = "{notBlank}")
	private String nome;

}
