package org.example.api.rest.api.model.dto.permissao;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissaoInputDto {

	@NotBlank(message = "{notBlank}")
	private String nome;

	@NotBlank(message = "{notBlank}")
	private String descricao;
}