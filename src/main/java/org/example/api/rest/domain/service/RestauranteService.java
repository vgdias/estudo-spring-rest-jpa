package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.CIDADE_POR_ID_NAO_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.COZINHA_POR_ID_NAO_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.RESTAURANTE_FORMA_PAGAMENTO_POR_ID_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.RESTAURANTE_FORMA_PAGAMENTO_POR_ID_NAO_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.PRODUTO_POR_NOME_ENCONTRADO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.PRODUTO_POR_ID_NAO_ENCONTRADO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.RESTAURANTE_POR_ID_EM_USO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.RESTAURANTE_POR_NOME_ENCONTRADO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.RESTAURANTE_POR_ID_NAO_ENCONTRADO;
import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comFreteGratis;
import static org.example.api.rest.infrastructure.repository.spec.RestauranteSpecs.comNomeSemelhante;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ValidationException;

import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.model.Endereco;
import org.example.api.rest.domain.model.FormaPagamento;
import org.example.api.rest.domain.model.Produto;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.repository.ProdutoRepository;
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
public class RestauranteService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	CidadeService cidadeService;

	@Autowired
	CozinhaService cozinhaService;

	@Autowired
	ProdutoService produtoService;

	@Autowired
	FormaPagamentoService formaPagamentoService;

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Transactional
	public void ativar(Long restauranteId) {
		Restaurante restauranteAtual = buscarRestaurantePorId(restauranteId);
		restauranteAtual.ativar();
	}

	@Transactional
	public void inativar(Long restauranteId) {
		Restaurante restauranteAtual = buscarRestaurantePorId(restauranteId);
		restauranteAtual.desativar();
	}

	public List<Restaurante> listar() {
		return restauranteRepository.findAll();
	}

	public Restaurante buscarPorId(Long restauranteId) {
		return buscarRestaurantePorId(restauranteId);
	}

	@Transactional
	public Restaurante adicionar(Restaurante restaurante) {
		verificarSeRestauranteNomeExiste(restaurante.getNome());

		Long cozinhaId = restaurante.getCozinha().getId();
		if (Objects.nonNull(cozinhaId)) {
			Cozinha cozinha = cozinhaService.buscarCozinhaPorId(cozinhaId);
			restaurante.setCozinha(cozinha);

			Long cidadeId = restaurante.getEndereco().getCidade().getId();
			if (Objects.nonNull(cidadeId)) {
				Cidade cidade  = cidadeService.buscarCidadePorId(cidadeId); 
				restaurante.getEndereco().setCidade(cidade);
			}
			return restauranteRepository.save(restaurante);

		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(
							COZINHA_POR_ID_NAO_ENCONTRADA.toString(), 
							restaurante.getCozinha().getId()));
		}
	}

	@Transactional
	public Restaurante alterar(Restaurante restauranteNovo) {
		entityManager.detach(restauranteNovo);
		verificarSeRestauranteNomeExiste(restauranteNovo.getNome());

		Long cozinhaId = restauranteNovo.getCozinha().getId();
		if (Objects.nonNull(cozinhaId)) {
			Cozinha cozinhaAtual = cozinhaService.buscarCozinhaPorId(cozinhaId);
			restauranteNovo.setCozinha(cozinhaAtual);
			return restauranteRepository.save(restauranteNovo);
		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(
							COZINHA_POR_ID_NAO_ENCONTRADA.toString(), 
							restauranteNovo.getCozinha().getId()));
		}
	}

	@Transactional
	public Restaurante alterarNomeEFrete(Restaurante nomeEFreteRestauranteNovo, Long restauranteAtualId) {
		Restaurante restauranteAtual = buscarRestaurantePorId(restauranteAtualId);
		verificarSeRestauranteNomeExiste(nomeEFreteRestauranteNovo.getNome());

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
					String.format(
							RESTAURANTE_POR_ID_NAO_ENCONTRADO.toString(), 
							restauranteId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(
							RESTAURANTE_POR_ID_EM_USO.toString(), 
							restauranteId));
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
		cozinhaService.buscarCozinhaPorId(cozinhaId);
		return restauranteRepository.countByCozinhaId(cozinhaId);
	}

	public int quantosRestaurantesPorCozinhaNome(String cozinhaNome) {
		cozinhaService.porNome(cozinhaNome);
		return restauranteRepository.countByCozinhaNome(cozinhaNome);
	}

	public List<Restaurante> restauranteComNomeSemelhanteECozinhaId(String nome, Long cozinhaId) {
		cozinhaService.buscarCozinhaPorId(cozinhaId);
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

	@Transactional
	public Restaurante alterarRestauranteEndereco(Endereco enderecoNovo, Long restauranteAtualId) {
		Restaurante restauranteAtual = buscarRestaurantePorId(restauranteAtualId);
		if (Objects.nonNull(enderecoNovo.getCidade().getId())) {
			Cidade cidadeAtual = cidadeService.buscarCidadePorId(enderecoNovo.getCidade().getId());
			enderecoNovo.setCidade(cidadeAtual);
			restauranteAtual.setEndereco(enderecoNovo);
			return restauranteRepository.save(restauranteAtual);
		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(
							CIDADE_POR_ID_NAO_ENCONTRADA.toString(), 
							enderecoNovo.getCidade().getId()));
		}
	}

	@Transactional
	public void excluirRestauranteFormaPagamento(Long restauranteId, Long formaPagamentoId) {
		Restaurante restaurante = buscarRestaurantePorId(restauranteId);
		FormaPagamento formaPagamento = formaPagamentoService.buscarFormaPagamentoPorId(formaPagamentoId);

		if (! restaurante.excluirFormaPagamento(formaPagamento)) {
			throw new RecursoNaoEncontradoException(
					String.format(
							RESTAURANTE_FORMA_PAGAMENTO_POR_ID_NAO_ENCONTRADA.toString(), 
							formaPagamentoId));
		}
	}

	@Transactional
	public void incluirRestauranteFormaPagamento(Long restauranteId, Long formaPagamentoId) {
		Restaurante restaurante = buscarRestaurantePorId(restauranteId);
		FormaPagamento formaPagamento = formaPagamentoService.buscarFormaPagamentoPorId(formaPagamentoId);

		if (! restaurante.incluirFormaPagamento(formaPagamento)) {
			throw new ValidationException(
					String.format(
							RESTAURANTE_FORMA_PAGAMENTO_POR_ID_ENCONTRADA.toString(), 
							formaPagamentoId));
		}
	}

	public Set<Produto> listarRestauranteProdutos(Restaurante restaurante) {
		return produtoRepository.findByRestaurante(restaurante);
	}

	public Produto buscarRestauranteProdutoPorId(Long restauranteId, Long produtoId) {
		return produtoRepository.obterProdutoDeRestaurante(restauranteId, produtoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								PRODUTO_POR_ID_NAO_ENCONTRADO.toString(), 
								produtoId)));
	}

	@Transactional
	public Produto adicionarRestauranteProduto(Long restauranteId, Produto produto) {
		Restaurante restaurante = buscarRestaurantePorId(restauranteId);
		if (verificarSeRestauranteProdutoNomeExiste(produto.getNome())) {
			throw new RecursoEmUsoException(
					String.format(
							PRODUTO_POR_NOME_ENCONTRADO.toString(), 
							produto.getNome())
					);
		}

		produto.setRestaurante(restaurante);
		return produtoService.adicionar(produto);
	}

	private Boolean verificarSeRestauranteProdutoNomeExiste(String nome) {
		return restauranteRepository.existsByProdutosNome(nome);
	}

	private void verificarSeRestauranteNomeExiste(String nome) {
		restauranteRepository.findByNome(nome)
		.ifPresent((restauranteEncontrado) -> { 
			throw new RecursoEmUsoException(
					String.format(
							RESTAURANTE_POR_NOME_ENCONTRADO.toString(), 
							restauranteEncontrado.getNome())
					);
		});
	}

	public Restaurante buscarRestaurantePorId(Long id) {
		return restauranteRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								RESTAURANTE_POR_ID_NAO_ENCONTRADO.toString(), 
								id)));
	}

}