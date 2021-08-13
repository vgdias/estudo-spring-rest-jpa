package org.example.api.rest.domain.service;

import javax.transaction.Transactional;

import org.example.api.rest.domain.model.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FluxoPedidoService {

	@Autowired
	private PedidoService emissaoPedido;

	@Transactional
	public void confirmar(Long pedidoId) {
		Pedido pedido = emissaoPedido.buscarPedidoPorId(pedidoId);
		pedido.confirmar();
	}

	@Transactional
	public void cancelar(Long pedidoId) {
		Pedido pedido = emissaoPedido.buscarPedidoPorId(pedidoId);
		pedido.cancelar();
	}

	@Transactional
	public void entregar(Long pedidoId) {
		Pedido pedido = emissaoPedido.buscarPedidoPorId(pedidoId);
		pedido.entregar();
	}
}