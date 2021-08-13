package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.RESTAURANTE_FORMA_PAGAMENTO_POR_ID_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.RESTAURANTE_FORMA_PAGAMENTO_POR_ID_NAO_ENCONTRADA;

import javax.validation.ValidationException;

import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.FormaPagamento;
import org.example.api.rest.domain.model.Restaurante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestauranteFormaPagamentoService {

	@Autowired
	FormaPagamentoService formaPagamentoService;

	@Autowired
	RestauranteService restauranteService;

	@Transactional
	public void excluirRestauranteFormaPagamento(Long restauranteId, Long formaPagamentoId) {
		Restaurante restaurante = restauranteService.buscarRestaurantePorId(restauranteId);
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
		Restaurante restaurante = restauranteService.buscarRestaurantePorId(restauranteId);
		FormaPagamento formaPagamento = formaPagamentoService.buscarFormaPagamentoPorId(formaPagamentoId);

		if (! restaurante.incluirFormaPagamento(formaPagamento)) {
			throw new ValidationException(
					String.format(
							RESTAURANTE_FORMA_PAGAMENTO_POR_ID_ENCONTRADA.toString(), 
							formaPagamentoId));
		}
	}
}