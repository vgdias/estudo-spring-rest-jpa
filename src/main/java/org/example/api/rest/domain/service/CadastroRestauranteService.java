package org.example.api.rest.domain.service;

import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comFreteGratis;
import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comNomeSemelhante;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.model.Endereco;
import org.example.api.rest.domain.model.Restaurante;
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
	private static final String MSG_RESTAURANTE_NAO_ENCONTRADO = "Restaurante de id %d nao encontrado";

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CadastroCozinhaService cozinhaService;

	@Autowired
	private CadastroCidadeService cidadeService;


	@Transactional
	public void ativar(Long restauranteId) {
		Restaurante restauranteAtual = obterRestaurante(restauranteId);
		restauranteAtual.ativar();
	}

	@Transactional
	public void inativar(Long restauranteId) {
		Restaurante restauranteAtual = obterRestaurante(restauranteId);
		restauranteAtual.desativar();
	}

	public List<Restaurante> listar() {
		return restauranteRepository.findAll();
	}

	public Restaurante buscar(Long restauranteId) {
		return obterRestaurante(restauranteId);
	}

	@Transactional
	public Restaurante adicionar(Restaurante restaurante) {
		Long cozinhaId = restaurante.getCozinha().getId();
		if (Objects.nonNull(cozinhaId)) {
			Cozinha cozinha = cozinhaService.obterCozinha(cozinhaId);

			restaurante.setCozinha(cozinha);

			Long cidadeId = restaurante.getEndereco().getCidade().getId();
			if (Objects.nonNull(cidadeId)) {
				Cidade cidade  = cidadeService.obterCidade(cidadeId); 
				restaurante.getEndereco().setCidade(cidade);
			}
			return restauranteRepository.save(restaurante);

		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, 
							restaurante.getCozinha().getId()));
		}
	}

	@Transactional
	public Restaurante alterar(Restaurante restauranteNovo) {
		if (Objects.nonNull(restauranteNovo.getCozinha().getId())) {
			Cozinha cozinhaAtual = cozinhaService.obterCozinha(restauranteNovo.getCozinha().getId());

			restauranteNovo.setCozinha(cozinhaAtual);
			return restauranteRepository.save(restauranteNovo);

		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, 
							restauranteNovo.getCozinha().getId()));
		}
	}

	@Transactional
	public Restaurante alterarNomeEFrete(Restaurante nomeEFreteRestauranteNovo, Long restauranteAtualId) {

		Restaurante restauranteAtual = obterRestaurante(restauranteAtualId);
		restauranteAtual.setNome(nomeEFreteRestauranteNovo.getNome());
		restauranteAtual.setTaxaFrete(nomeEFreteRestauranteNovo.getTaxaFrete());
		return restauranteRepository.save(restauranteAtual);
	}

	@Transactional
	public void remover(Long restauranteId) {
		try {
			restauranteRepository.deleteById(restauranteId);
			restauranteRepository.flush();

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
		cozinhaService.obterCozinha(cozinhaId);
		return restauranteRepository.countByCozinhaId(cozinhaId);
	}

	public int quantosRestaurantesPorCozinhaNome(String cozinhaNome) {
		cozinhaService.porNome(cozinhaNome);
		return restauranteRepository.countByCozinhaNome(cozinhaNome);
	}

	public List<Restaurante> restauranteComNomeSemelhanteECozinhaId(String nome, Long cozinhaId) {
		cozinhaService.obterCozinha(cozinhaId);
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

	public Restaurante obterRestaurante(Long id) {
		return restauranteRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_RESTAURANTE_NAO_ENCONTRADO, id)));
	}

	public Restaurante alterarEnderecoDeRestaurante(Endereco enderecoNovo, Long restauranteAtualId) {
		Restaurante restauranteAtual = obterRestaurante(restauranteAtualId);
		if (Objects.nonNull(enderecoNovo.getCidade().getId())) {
			Cidade cidadeAtual = cidadeService.obterCidade(enderecoNovo.getCidade().getId());
			enderecoNovo.setCidade(cidadeAtual);
			restauranteAtual.setEndereco(enderecoNovo);
			return restauranteRepository.save(restauranteAtual);
		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, 
							enderecoNovo.getCidade().getId()));
		}
	}
}