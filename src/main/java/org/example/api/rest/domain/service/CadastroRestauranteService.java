package org.example.api.rest.domain.service;

import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comFreteGratis;
import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comNomeSemelhante;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.repository.CozinhaRepository;
import org.example.api.rest.domain.repository.RestauranteRepository;
import org.example.api.rest.infrastructure.repository.spec.RestauranteComFreteGratisSpec;
import org.example.api.rest.infrastructure.repository.spec.RestauranteComNomeSemelhanteSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroRestauranteService {

	private static final String MSG_RESTAURANTE_EM_USO = "Restaurante de id %d em uso e nao pode ser removido";
	private static final String MSG_COZINHA_POR_ID_NAO_ENCONTRADA = "Cozinha de id %d nao encontrada";
	private static final String MSG_COZINHA_POR_NOME_NAO_ENCONTRADA = "Cozinha de nome %s nao encontrada";
	private static final String MSG_RESTAURANTE_NAO_ENCONTRADO = "Restaurante de id %d nao encontrado";

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CozinhaRepository cozinhaRepository;

	public List<Restaurante> listar() {
		return restauranteRepository.findAll();
	}

	public Restaurante buscar(Long restauranteId) {
		return obtemRestaurante(restauranteId);
	}

	@Transactional
	public Restaurante adicionar(Restaurante restaurante) {
		if (Objects.nonNull(restaurante.getCozinha().getId())) {
			Cozinha cozinha = obtemCozinhaDeRestaurante(restaurante.getCozinha().getId());

			restaurante.setCozinha(cozinha);
			return restauranteRepository.save(restaurante);

		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, 
							restaurante.getCozinha().getId()));
		}
	}

	@Transactional
	public Restaurante alterar(Restaurante restauranteNovo) {

		//		if (propriedadesRestauranteNovo.isEmpty()) {
		//			throw new ValidationException("Nenhuma propriedade foi fornecida");
		//		}
		//		if (propriedadesRestauranteNovo.containsKey("id")) {
		//			throw new ValidationException("A propriedade 'restaurante.id' nao pode ser alterada");
		//		}

//		Restaurante restauranteAtual = obtemRestaurante(restauranteAtualId, 
//				MSG_RESTAURANTE_NAO_ENCONTRADO);
//
//		GenericMapper.map(propriedadesRestauranteNovo, restauranteAtual, Restaurante.class, request);

		//		if ( (Objects.nonNull(restauranteAtual.getCozinha()) ) 
		//				&& (Objects.nonNull(restauranteAtual.getCozinha().getId()))) {

		Long cozinhaAtualId = restauranteNovo.getCozinha().getId();
		Cozinha cozinhaAtual = obtemCozinhaDeRestaurante(cozinhaAtualId);

		//			if (restauranteAtual.getNome().trim().isEmpty()) {
		//				throw new ValidationException("A propriedade 'nome' nao pode ser vazia");
		//			}
		//			if (Objects.isNull(restauranteAtual.getTaxaFrete())) {
		//				throw new ValidationException("A propriedade 'taxaFrete' nao pode ser vazia");
		//			}

		restauranteNovo.setCozinha(cozinhaAtual);
		return restauranteRepository.save(restauranteNovo);
		//		} else {
		//			throw new DependenciaNaoEncontradaException(
		//					String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, restauranteAtual.getCozinha().getId()));
		//		}
	}

	@Transactional
	public Restaurante alterarNomeEFrete(Restaurante nomeEFreteRestauranteNovo, Long restauranteAtualId) {

		Restaurante restauranteAtual = obtemRestaurante(restauranteAtualId);
		restauranteAtual.setNome(nomeEFreteRestauranteNovo.getNome());
		restauranteAtual.setTaxaFrete(nomeEFreteRestauranteNovo.getTaxaFrete());
		return restauranteRepository.save(restauranteAtual);
	}

	//	@Transactional
	public void remover(Long restauranteId) {
		try {

			restauranteRepository.deleteById(restauranteId);

		} catch (EmptyResultDataAccessException e) {
			throw new RecursoNaoEncontradoException(
					String.format(MSG_RESTAURANTE_NAO_ENCONTRADO, restauranteId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
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
		obtemCozinhaDeRestaurante(cozinhaId);
		return restauranteRepository.countByCozinhaId(cozinhaId);
	}

	public int quantosRestaurantesPorCozinhaNome(String cozinhaNome) {
		cozinhaRepository.findByNome(cozinhaNome)
		.orElseThrow(() -> new DependenciaNaoEncontradaException(
				String.format(MSG_COZINHA_POR_NOME_NAO_ENCONTRADA, cozinhaNome)));

		return restauranteRepository.countByCozinhaNome(cozinhaNome);
	}

	public List<Restaurante> restauranteComNomeSemelhanteECozinhaId(String nome, Long cozinhaId) {
		obtemCozinhaDeRestaurante(cozinhaId);
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

	public Restaurante obtemRestaurante(Long id) {
		return restauranteRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_RESTAURANTE_NAO_ENCONTRADO, id)));
	}

	public Cozinha obtemCozinhaDeRestaurante(Long id) {
		return cozinhaRepository.findById(id)
				.orElseThrow(() -> new DependenciaNaoEncontradaException(
						String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, id)));
	}
}