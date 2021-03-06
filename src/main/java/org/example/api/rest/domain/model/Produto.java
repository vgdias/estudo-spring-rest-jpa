package org.example.api.rest.domain.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.groups.Default;

import org.example.api.rest.shared.validation.Groups.AlterarProduto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="produto")
public class Produto {

	@NotNull(groups = AlterarProduto.class, message = "{notNull}")
	@Positive(groups = AlterarProduto.class)
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(groups = {Default.class, AlterarProduto.class}, message = "{notBlank}")
	@Column(nullable = false)
	private String nome;

	@NotBlank(groups = {Default.class, AlterarProduto.class}, message = "{notBlank}")
	@Column(nullable = false)
	private String descricao;

	@NotNull(groups = {Default.class, AlterarProduto.class}, message = "{notBlank}")
	@PositiveOrZero
	@Column(nullable = false)
	private BigDecimal preco;

	private Boolean ativo = Boolean.TRUE;

	@NotNull
	@Valid
	@ManyToOne
	@JoinColumn(nullable = false)
	private Restaurante restaurante;
}