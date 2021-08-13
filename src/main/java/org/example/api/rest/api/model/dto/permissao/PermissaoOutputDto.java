package org.example.api.rest.api.model.dto.permissao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissaoOutputDto {
	private Long id;
	private String nome;
	private String descricao;
}
