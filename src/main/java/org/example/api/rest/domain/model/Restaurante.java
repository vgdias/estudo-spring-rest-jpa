package org.example.api.rest.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.groups.Default;

import org.example.api.rest.shared.validation.Groups.AlterarRestaurante;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="restaurante")
public class Restaurante {

	@NotNull(groups = {AlterarRestaurante.class}, message = "{notNull}")
	@Positive(groups = {AlterarRestaurante.class}, message = "{positive}")
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(groups = {Default.class, AlterarRestaurante.class}, message = "{notBlank}")
	@Column(nullable = false)
	private String nome;

	@NotNull(groups = {Default.class, AlterarRestaurante.class}, message = "{notNull}")
	@PositiveOrZero(groups = {Default.class, AlterarRestaurante.class}, message = "{positiveOrZero}")
	@Column(name = "taxa_frete", nullable = false)
	private BigDecimal taxaFrete;

	// https://stackoverflow.com/questions/24994440/no-serializer-found-for-class-org-hibernate-proxy-pojo-javassist-javassist
	// @JsonIgnoreProperties("hibernateLazyInitializer")
	@Valid
	@NotNull(groups = {Default.class, AlterarRestaurante.class}, message = "{notNull}")
	@ManyToOne
	@JoinColumn(name = "cozinha_id", nullable = false)
	private Cozinha cozinha;

	@Valid
	@NotNull(groups = {Default.class, AlterarRestaurante.class}, message = "{notNull}")
	@Embedded
	private Endereco endereco;

	@CreationTimestamp
	@Column(name = "data_cadastro", nullable = false, columnDefinition = "datetime")
	private OffsetDateTime dataCadastro;

	@UpdateTimestamp
	@Column(name = "data_atualizacao", nullable = false, columnDefinition = "datetime")
	private OffsetDateTime dataAtualizacao;

	@ManyToMany
	@JoinTable(name = "restaurante_forma_pagamento",
	joinColumns = @JoinColumn(name = "restaurante_id"),
	inverseJoinColumns = @JoinColumn(name = "forma_pagamento_id"))
	private Set<FormaPagamento> formasPagamento = new HashSet<>();

	@OneToMany(mappedBy = "restaurante")
	private Set<Produto> produtos = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "restaurante_usuario_responsavel",
	joinColumns = @JoinColumn(name = "restaurante_id"),
	inverseJoinColumns = @JoinColumn(name = "usuario_id"))
	private Set<Usuario> responsaveis = new HashSet<>();    

	private Boolean ativo = Boolean.TRUE;
	private Boolean aberto = Boolean.FALSE;

	public void ativar() {
		setAtivo(true);
	}

	public void desativar() {
		setAtivo(false);
	}

	public void abrir() {
		setAberto(true);
	}

	public void fechar() {
		setAberto(false);
	}  

	public boolean incluirFormaPagamento(FormaPagamento formaPagamento) {
		return getFormasPagamento().add(formaPagamento);
	}

	public boolean excluirFormaPagamento(FormaPagamento formaPagamento) {
		return getFormasPagamento().remove(formaPagamento);
	}

	public boolean aceitaFormaPagamento(FormaPagamento formaPagamento) {
	    return getFormasPagamento().contains(formaPagamento);
	}

	public boolean naoAceitaFormaPagamento(FormaPagamento formaPagamento) {
	    return ! aceitaFormaPagamento(formaPagamento);
	}
	
	public boolean removerResponsavel(Usuario usuario) {
		return getResponsaveis().remove(usuario);
	}

	public boolean adicionarResponsavel(Usuario usuario) {
		return getResponsaveis().add(usuario);
	}
}