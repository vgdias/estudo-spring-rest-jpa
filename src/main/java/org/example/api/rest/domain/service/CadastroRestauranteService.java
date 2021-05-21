package org.example.api.rest.domain.service;

import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comFreteGratis;
import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comNomeSemelhante;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.EntidadeEmUsoException;
import org.example.api.rest.domain.exception.EntidadeNaoEncontradaException;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.repository.CozinhaRepository;
import org.example.api.rest.domain.repository.RestauranteRepository;
import org.example.api.rest.infrastructure.repository.spec.RestauranteComFreteGratisSpec;
import org.example.api.rest.infrastructure.repository.spec.RestauranteComNomeSemelhanteSpec;
import org.example.api.rest.shared.mapper.GenericMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroRestauranteService {

	private static final String MSG_RESTAURANTE_EM_USO = "Restaurante de codigo %d em uso e nao pode ser removido";
	private static final String MSG_COZINHA_POR_ID_NAO_ENCONTRADA = "Cozinha de id %d nao encontrada";
	private static final String MSG_COZINHA_POR_NOME_NAO_ENCONTRADA = "Cozinha de nome %s nao encontrada";
	private static final String MSG_RESTAURANTE_NAO_ENCONTRADO = "Restaurante de codigo %d nao encontrado";

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CozinhaRepository cozinhaRepository;

	public List<Restaurante> listar() {
		return restauranteRepository.findAll();
	}

	public Restaurante buscar(Long restauranteId) {
		return restauranteRepository.findById(restauranteId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_RESTAURANTE_NAO_ENCONTRADO, restauranteId)));
	}

	@Transactional
	public Restaurante adicionar(Restaurante restaurante) {
		Cozinha cozinha = cozinhaRepository.findById(restaurante.getCozinha().getId())
				.orElseThrow(() -> new DependenciaNaoEncontradaException(
						String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, restaurante.getCozinha().getId())));

		restaurante.setCozinha(cozinha);
		return restauranteRepository.save(restaurante);
	}

	@Transactional
	public Restaurante alterar(Map<String, Object> propriedadesRestauranteNovo, Long restauranteAtualId) {

		Restaurante restauranteAtual = restauranteRepository.findById(restauranteAtualId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_RESTAURANTE_NAO_ENCONTRADO, restauranteAtualId)));

		GenericMapper.map(propriedadesRestauranteNovo, restauranteAtual, Restaurante.class);
		Long cozinhaAtualId = restauranteAtual.getCozinha().getId();

		Cozinha cozinhaAtual = cozinhaRepository.findById(cozinhaAtualId)
				.orElseThrow(() -> new DependenciaNaoEncontradaException(
						String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, cozinhaAtualId)));

		restauranteAtual.setCozinha(cozinhaAtual);
		return restauranteRepository.save(restauranteAtual);
	}

	@Transactional
	public void remover(Long restauranteId) {
		try {
			restauranteRepository.deleteById(restauranteId);

		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format(MSG_RESTAURANTE_NAO_ENCONTRADO, restauranteId));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_RESTAURANTE_EM_USO, restauranteId));
		}
	}

	public List<Restaurante> restauranteComFreteGratisComNomeSemelhante(String nome) {
		Specification<Restaurante> comFreteGratis, comNomeSemelhante;
		comFreteGratis = new RestauranteComFreteGratisSpec();
		comNomeSemelhante = new RestauranteComNomeSemelhanteSpec(nome);

		return restauranteRepository.findAll(comFreteGratis.and(comNomeSemelhante));
	}

	public List<Restaurante> restaurantePorIntervaloDeTaxaFrete(BigDecimal taxaInicial, BigDecimal taxaFinal) {
		return restauranteRepository.findByTaxaFreteBetween(taxaInicial, taxaFinal);
	}

	public int quantosRestaurantesPorCozinhaId(Long cozinhaId) {
		cozinhaRepository.findById(cozinhaId)
		.orElseThrow(() -> new DependenciaNaoEncontradaException(
				String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, cozinhaId)));

		return restauranteRepository.countByCozinhaId(cozinhaId);
	}

	public int quantosRestaurantesPorCozinhaNome(String cozinhaNome) {
		cozinhaRepository.findByNome(cozinhaNome)
		.orElseThrow(() -> new DependenciaNaoEncontradaException(
				String.format(MSG_COZINHA_POR_NOME_NAO_ENCONTRADA, cozinhaNome)));

		return restauranteRepository.countByCozinhaNome(cozinhaNome);
	}

	public List<Restaurante> restauranteComNomeSemelhanteECozinhaId(String nome, Long cozinhaId) {
		cozinhaRepository.findById(cozinhaId)
		.orElseThrow(() -> new DependenciaNaoEncontradaException(
				String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, cozinhaId)));

		return restauranteRepository.nomeContainingAndCozinhaId(nome, cozinhaId);
	}

	public List<Restaurante> restauranteComNomeSemelhante(String nome) {
		return restauranteRepository.comNomeSemelhante(nome);
	}

	public List<Restaurante> buscaCustomizadaPorNomeEFrete(String nome, BigDecimal taxaFreteInicial,
			BigDecimal taxaFreteFinal) {
		return restauranteRepository.buscaCustomizadaPorNomeEFrete(nome, taxaFreteInicial, taxaFreteFinal);
	}

	public List<Restaurante> buscaDinamica(String nome, BigDecimal taxaFreteInicial,
			BigDecimal taxaFreteFinal, String nomeCozinha) {
		return restauranteRepository.buscaDinamica(nome, taxaFreteInicial, taxaFreteFinal, nomeCozinha);
	}

	public List<Restaurante> restaurantesComFreteGratisENomeSemelhanteSpec(String nome) {
		Specification<Restaurante> comFreteGratis = new RestauranteComFreteGratisSpec();
		Specification<Restaurante> comNomeSemelhante = new RestauranteComNomeSemelhanteSpec(nome);

		return restauranteRepository.findAll(comFreteGratis.and(comNomeSemelhante));
	}

	public List<Restaurante> restaurantesComFreteGratisENomeSemelhanteSpec2(String nome) {
		return restauranteRepository.findAll(comFreteGratis().and(comNomeSemelhante(nome)));
	}

}