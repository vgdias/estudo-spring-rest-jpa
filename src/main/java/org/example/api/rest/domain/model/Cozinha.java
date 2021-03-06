package org.example.api.rest.domain.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.groups.Default;

import org.example.api.rest.shared.validation.Groups.AlterarCozinha;
import org.example.api.rest.shared.validation.Groups.AlterarRestaurante;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name="cozinha")
public class Cozinha {

	@NotNull(groups = {AlterarCozinha.class, AlterarRestaurante.class}, message = "{notNull}")
	@Positive(groups = {AlterarCozinha.class, AlterarRestaurante.class}, message = "{positive}")
	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(groups = {Default.class, AlterarCozinha.class}, message = "{notBlank}")
	@Column(nullable = false)
	private String nome;
	
	@OneToMany(mappedBy = "cozinha")
	private Set<Restaurante> restaurantes = new HashSet<>();
	
}