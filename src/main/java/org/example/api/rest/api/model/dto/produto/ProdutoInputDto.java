package org.example.api.rest.api.model.dto.produto;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProdutoInputDto {

	@NotBlank(message = "{notBlank}")
	private String nome;

	@NotBlank(message = "{notBlank}")
	private String descricao;

	@NotNull(message = "{notNull}")
	private BigDecimal preco;
}