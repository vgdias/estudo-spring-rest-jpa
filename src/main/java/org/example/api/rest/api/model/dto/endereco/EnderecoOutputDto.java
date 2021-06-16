package org.example.api.rest.api.model.dto.endereco;

import org.example.api.rest.api.model.dto.cidade.ResumoCidadeOutputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoOutputDto {
	private String cep;
	private String logradouro;
	private String numero;
	private String complemento;
	private String bairro;
	private ResumoCidadeOutputDto cidade;
}
