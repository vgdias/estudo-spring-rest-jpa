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

	@NotNull(groups = AlterarProduto.class)
	@Positive(groups = AlterarProduto.class)
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(groups = {Default.class, AlterarProduto.class})
	@Column(nullable = false)
	private String nome;

	@NotBlank(groups = {Default.class, AlterarProduto.class})
	@Column(nullable = false)
	private String descricao;

	@NotBlank(groups = {Default.class, AlterarProduto.class})
	@PositiveOrZero
	@Column(nullable = false)
	private BigDecimal preco;

	@NotNull(groups = {Default.class, AlterarProduto.class})
	@Column(nullable = false)
	private Boolean ativo;

	@Valid
	@ManyToOne
	@JoinColumn(name = "restauranteId")
	private Restaurante restaurante;

}