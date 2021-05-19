package org.example.api.rest.infrastructure.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.repository.RestauranteCustomRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class RestauranteRepositoryImpl implements RestauranteCustomRepository {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public List<Restaurante> buscaCustomizadaPorNomeEFrete(String nome, BigDecimal taxaFreteInicial, 
			BigDecimal taxaFreteFinal) {

		String jpql = "from Restaurante "
				+ "where nome like :nome "
				+ "and taxaFrete between :taxaInicial and :taxaFinal";

		return manager.createQuery(jpql, Restaurante.class)
				.setParameter("nome", "%" + nome + "%")
				.setParameter("taxaInicial", taxaFreteInicial)
				.setParameter("taxaFinal", taxaFreteFinal)
				.getResultList();
	}

	@Override
	public List<Restaurante> buscaDinamica(String nome, BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal, 
			String nomeCozinha) {
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Restaurante> criteria = builder.createQuery(Restaurante.class);
		Root<Restaurante> root = criteria.from(Restaurante.class);

		List<Predicate> predicates = new ArrayList<Predicate>();
		if (StringUtils.hasText(nome)) {
			predicates.add(builder.like(root.get("nome"), "%" + nome + "%"));
		}

		if (taxaFreteInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("taxaFrete"), taxaFreteInicial));
		}

		if (taxaFreteFinal != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get("taxaFrete"), taxaFreteFinal));
		}
		
		if (StringUtils.hasText(nomeCozinha)) {
			predicates.add(builder.like(root.get("cozinha").get("nome"), "%" + nomeCozinha + "%"));
		}

		criteria.where(predicates.toArray(new Predicate[0]));

		TypedQuery<Restaurante> query = manager.createQuery(criteria);
		return query.getResultList();

	}
}
