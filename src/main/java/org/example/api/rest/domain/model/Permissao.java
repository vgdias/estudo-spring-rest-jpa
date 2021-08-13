package org.example.api.rest.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.groups.Default;

import org.example.api.rest.shared.validation.Groups.AlterarFormaPagamento;
import org.example.api.rest.shared.validation.Groups.AlterarPermissao;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="permissao")
public class Permissao {

	@NotNull(groups = {AlterarPermissao.class}, message = "{notNull}")
	@Positive(groups = {AlterarPermissao.class}, message = "{positive}")
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(groups = {Default.class, AlterarFormaPagamento.class}, message = "{notBlank}")
	@Column(nullable = false)
	private String nome;

	@NotBlank(groups = {Default.class, AlterarFormaPagamento.class}, message = "{notBlank}")
	@Column(nullable = false)
	private String descricao;

}