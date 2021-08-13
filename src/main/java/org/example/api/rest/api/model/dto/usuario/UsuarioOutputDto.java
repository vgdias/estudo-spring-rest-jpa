package org.example.api.rest.api.model.dto.usuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioOutputDto {
	private Long id;
	private String nome;
	private String email;
}