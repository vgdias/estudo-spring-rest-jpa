package org.example.api.rest.api.model.dto.cidade;

import org.example.api.rest.api.model.dto.estado.EstadoOutputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CidadeOutputDto {
	private Long id;
	private String nomeCidade;
	private EstadoOutputDto estado;
}
