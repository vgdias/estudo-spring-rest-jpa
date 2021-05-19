package org.example.api.rest.domain.service;

import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comFreteGratis;
import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comNomeSemelhante;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CadastroRestauranteService {

	private static final String MSG_RESTAURANTE_EM_USO = "Restaurante de codigo %d em uso e nao pode ser removido";
	private static final String MSG_COZINHA_NAO_ENCONTRADA = "Cozinha de codigo %d nao encontrada";
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
		Optional<Cozinha> cozinhaOpt = cozinhaRepository.findById(restaurante.getCozinha().getId());
		if (cozinhaOpt.isPresent()) {
			restaurante.setCozinha(cozinhaOpt.get());
			return restauranteRepository.save(restaurante);
		}
		throw new EntidadeNaoEncontradaException(
				String.format(MSG_COZINHA_NAO_ENCONTRADA, restaurante.getCozinha().getId()));
	}

	@Transactional
	public Restaurante alterar(Map<String, Object> propriedadesRestauranteNovo, Long restauranteAtualId) {

		Optional<Restaurante> restauranteAtualOpt = restauranteRepository.findById(restauranteAtualId);
		if (restauranteAtualOpt.isPresent()) {
			Restaurante restauranteAtual = restauranteAtualOpt.get();
			GenericMapper.map(propriedadesRestauranteNovo, restauranteAtual, Restaurante.class);

			Optional<Cozinha> cozinhaAtualOpt = cozinhaRepository.findById(restauranteAtual.getCozinha().getId());
			if (cozinhaAtualOpt.isPresent()) {
				restauranteAtual.setCozinha(cozinhaAtualOpt.get());
			} else {
				throw new EntidadeNaoEncontradaException(
						String.format(MSG_COZINHA_NAO_ENCONTRADA, restauranteAtual.getCozinha().getId()));
			}
			return restauranteRepository.save(restauranteAtual);
		}
		throw new EntidadeNaoEncontradaException(String.format(MSG_RESTAURANTE_NAO_ENCONTRADO, restauranteAtualId));
	}

	// Utilizando shared.mapper.GenericMapper
	@SuppressWarnings("unused")
	private void merge(Map<String, Object> propriedadesRestauranteNovo, Restaurante restauranteAtual) {
		// remove a propriedade id se houver, para que o restauranteAtual nao tenha seu id sobrescrito
		propriedadesRestauranteNovo.remove("id");

		// converte os elementos do Map propriedadesRestauranteNovo em um objeto Restaurante
		Restaurante restauranteNovo = new ObjectMapper().convertValue(propriedadesRestauranteNovo, Restaurante.class);

		propriedadesRestauranteNovo.forEach((nomePropriedade, valor) -> {
			// obtem dinamicamente uma propriedade da classe Restaurante pelo nome dela
			Field propriedade = ReflectionUtils.findField(Restaurante.class, nomePropriedade);

			// se a propriedade obtida for privada, eh preciso torna-la acessivel
			propriedade.setAccessible(true);

			// obtem o valor da propriedade obtida
			Object valorPropriedade = ReflectionUtils.getField(propriedade, restauranteNovo);

			// atribui dinamicamente o valor da propriedade obtida no objeto restauranteDestino
			ReflectionUtils.setField(propriedade, restauranteAtual, valorPropriedade);
		});
	}

	@Transactional
	public void remover(Long restauranteId) {
		try {
			Optional<Restaurante> restauranteOpt = restauranteRepository.findById(restauranteId);
			if (restauranteOpt.isPresent()) {
				restauranteRepository.delete(restauranteOpt.get());
			} else {
				throw new EntidadeNaoEncontradaException(String.format(MSG_RESTAURANTE_NAO_ENCONTRADO, restauranteId));
			}
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
		Optional<Cozinha> cozinhaOpt = cozinhaRepository.findById(cozinhaId);

		if (cozinhaOpt.isPresent()) {
			return restauranteRepository.countByCozinhaId(cozinhaId);
		}
		throw new EntidadeNaoEncontradaException(
				String.format(MSG_COZINHA_NAO_ENCONTRADA, cozinhaId));
	}

	public int quantosRestaurantesPorCozinhaNome(String cozinhaNome) {
		Optional<Cozinha> cozinhaOpt = cozinhaRepository.findByNome(cozinhaNome);

		if (cozinhaOpt.isPresent()) {
			return restauranteRepository.countByCozinhaNome(cozinhaNome);
		}
		throw new EntidadeNaoEncontradaException(
				String.format(MSG_COZINHA_NAO_ENCONTRADA, cozinhaOpt.get().getId()));
	}

	public List<Restaurante> restauranteComNomeSemelhanteECozinhaId(String nome, Long cozinhaId) {
		Optional<Cozinha> cozinhaOpt = cozinhaRepository.findById(cozinhaId);

		if (cozinhaOpt.isPresent()) {
			return restauranteRepository.nomeContainingAndCozinhaId(nome, cozinhaId);
		}
		throw new EntidadeNaoEncontradaException(
				String.format(MSG_COZINHA_NAO_ENCONTRADA, cozinhaId));
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