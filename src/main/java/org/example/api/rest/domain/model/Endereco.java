package org.example.api.rest.domain.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.example.api.rest.shared.validation.Groups.AlterarEndereco;

import lombok.Data;

@Data
@Embeddable
public class Endereco {

	@NotBlank(groups = {AlterarEndereco.class}, message = "{notBlank}")
	@Column(name = "endereco_cep")
	private String cep;

	@NotBlank(groups = {AlterarEndereco.class}, message = "{notBlank}")
	@Column(name = "endereco_logradouro")
	private String logradouro;

	@NotBlank(groups = {AlterarEndereco.class}, message = "{notBlank}")
	@Column(name = "endereco_numero")
	private String numero;

	@Column(name = "endereco_complemento")
	private String complemento;

	@NotBlank(groups = {AlterarEndereco.class}, message = "{notBlank}")
	@Column(name = "endereco_bairro")
	private String bairro;

	@Valid
	@NotNull(groups = {AlterarEndereco.class}, message = "{notNull}")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "endereco_cidade_id")
	private Cidade cidade;

}