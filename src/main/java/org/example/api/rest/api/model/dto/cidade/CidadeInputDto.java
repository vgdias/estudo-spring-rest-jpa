package org.example.api.rest.api.model.dto.cidade;

import org.example.api.rest.api.model.dto.estado.EstadoInputIdDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CidadeInputDto {
	private String nome;
	private EstadoInputIdDto estado;
}
