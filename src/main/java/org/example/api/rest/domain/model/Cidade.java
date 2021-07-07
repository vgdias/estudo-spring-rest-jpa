package org.example.api.rest.domain.model;

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
import javax.validation.groups.Default;

import org.example.api.rest.shared.validation.Groups.AlterarCidade;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="cidade")
public class Cidade {

	@NotNull(groups = AlterarCidade.class, message = "{notNull}")
	@Positive(groups = AlterarCidade.class, message = "{positive}")
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(groups = {Default.class, AlterarCidade.class}, message = "{notBlank}")
	@Column(nullable = false)
	private String nome;
	
	@Valid
	@NotNull(groups = {Default.class, AlterarCidade.class}, message = "{notNull}")
	@ManyToOne
	@JoinColumn(nullable = false)
	private Estado estado;

}