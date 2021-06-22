package org.example.api.rest.api.model.dto.cidade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CidadeOutputDto {
	private Long id;
	private String cidade;
	private String estado;
}
