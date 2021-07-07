package org.example.api.rest.api.model.dto.endereco;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.example.api.rest.api.model.dto.cidade.IdCidadeInputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoInputDto {
	@NotBlank(message = "{notBlank}")
	private String cep;
	
	@NotBlank(message = "{notBlank}")
	private String logradouro;
	
	@NotBlank(message = "{notBlank}")
	private String numero;

	@NotBlank(message = "{notBlank}")
	private String bairro;

	private String complemento;
	
	@Valid
	@NotNull(message = "{notNull}")
	private IdCidadeInputDto cidade;
}
