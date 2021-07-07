package org.example.api.rest.api.model.dto.cidade;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdCidadeInputDto {
	@NotNull(message = "{notNull}")
	private Long id;
}
