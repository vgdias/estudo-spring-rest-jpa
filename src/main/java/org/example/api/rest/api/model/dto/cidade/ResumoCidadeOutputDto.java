package org.example.api.rest.api.model.dto.cidade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumoCidadeOutputDto {
	private Long id;
	private String nomeCidade;
	private String estado;
}
