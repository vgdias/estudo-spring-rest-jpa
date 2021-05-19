package org.example.api.rest.infrastructure.repository.spec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.example.api.rest.domain.model.Restaurante;
import org.springframework.data.jpa.domain.Specification;

public class RestauranteComNomeSemelhanteSpec implements Specification<Restaurante> {

	private static final long serialVersionUID = 1L;

	private String nome;

	public RestauranteComNomeSemelhanteSpec(String nome) {
		this.nome = nome;
	}

	@Override
	public Predicate toPredicate(Root<Restaurante> root, CriteriaQuery<?> query, 
			CriteriaBuilder criteriaBuilder) {

		return criteriaBuilder.like(root.get("nome"), "%" + nome + "%");
	}


}
