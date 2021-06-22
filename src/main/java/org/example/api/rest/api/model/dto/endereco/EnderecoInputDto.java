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
	@NotBlank
	private String cep;
	
	@NotBlank
	private String logradouro;
	
	@NotBlank
	private String numero;

	@NotBlank
	private String bairro;

	private String complemento;
	
	@Valid
	@NotNull
	private IdCidadeInputDto cidade;
}
