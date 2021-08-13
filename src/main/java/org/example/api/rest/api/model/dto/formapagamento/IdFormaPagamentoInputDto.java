package org.example.api.rest.api.model.dto.formapagamento;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdFormaPagamentoInputDto {
	@Positive(message = "{positive}")
	@NotNull(message = "{notNull}")
	private Long id;
}
