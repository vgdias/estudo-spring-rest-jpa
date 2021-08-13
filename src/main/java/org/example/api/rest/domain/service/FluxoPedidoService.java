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
	public void confirmar(String codigo) {
		Pedido pedido = emissaoPedido.buscarPedidoPorId(codigo);
		pedido.confirmar();
	}

	@Transactional
	public void cancelar(String codigo) {
		Pedido pedido = emissaoPedido.buscarPedidoPorId(codigo);
		pedido.cancelar();
	}

	@Transactional
	public void entregar(String codigo) {
		Pedido pedido = emissaoPedido.buscarPedidoPorId(codigo);
		pedido.entregar();
	}
}