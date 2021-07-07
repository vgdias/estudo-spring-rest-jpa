package org.example.api.rest.api.model.dto.formapagamento;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormaPagamentoInputDto {

	@NotBlank(message = "{notBlank}")
	private String descricao;
}
