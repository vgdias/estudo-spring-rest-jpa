package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.PEDIDO_POR_ID_NAO_ENCONTRADO;

import java.util.List;

import javax.transaction.Transactional;

import org.example.api.rest.domain.exception.NegocioException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.model.FormaPagamento;
import org.example.api.rest.domain.model.Pedido;
import org.example.api.rest.domain.model.Produto;
import org.example.api.rest.domain.model.Restaurante;
import org.example.api.rest.domain.model.Usuario;
import org.example.api.rest.domain.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private RestauranteService restauranteService;

	@Autowired
	private CidadeService cidadeService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private RestauranteProdutoService restauranteProdutoService;

	@Autowired
	private FormaPagamentoService formaPagamentoService;

	public List<Pedido> listar() {
		return pedidoRepository.findAll();
	}

	@Transactional
	public Pedido emitir(Pedido pedido) {
		validarPedido(pedido);
		validarItens(pedido);

		pedido.setTaxaFrete(pedido.getRestaurante().getTaxaFrete());
		pedido.calcularValorTotal();

		return pedidoRepository.save(pedido);
	}

	private void validarPedido(Pedido pedido) {
		Cidade cidade = cidadeService.buscarCidadePorId(pedido.getEnderecoEntrega().getCidade().getId());
		Usuario cliente = usuarioService.buscarUsuarioPorId(pedido.getCliente().getId());
		Restaurante restaurante = restauranteService.buscarRestaurantePorId(pedido.getRestaurante().getId());
		FormaPagamento formaPagamento = formaPagamentoService.buscarFormaPagamentoPorId(pedido.getFormaPagamento().getId());

		pedido.getEnderecoEntrega().setCidade(cidade);
		pedido.setCliente(cliente);
		pedido.setRestaurante(restaurante);
		pedido.setFormaPagamento(formaPagamento);

		if (restaurante.naoAceitaFormaPagamento(formaPagamento)) {
			throw new NegocioException(String.format("Forma de pagamento '%s' não é aceita por esse restaurante.",
					formaPagamento.getDescricao()));
		}
	}

	private void validarItens(Pedido pedido) {
		pedido.getItens().forEach(item -> {
			Produto produto = restauranteProdutoService.buscarRestauranteProdutoPorId(
					pedido.getRestaurante().getId(), item.getProduto().getId());

			item.setPedido(pedido);
			item.setProduto(produto);
			item.setPrecoUnitario(produto.getPreco());
		});
	}

	public Pedido buscarPedidoPorId(Long id) {
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								PEDIDO_POR_ID_NAO_ENCONTRADO.toString(), 
								id)));
	}
}