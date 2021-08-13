package org.example.api.rest.api.model.dto.pedido;

import java.math.BigDecimal;

import org.example.api.rest.api.model.dto.restaurante.RestauranteResumoOutputDto;
import org.example.api.rest.api.model.dto.usuario.UsuarioOutputDto;
import org.example.api.rest.domain.model.StatusPedido;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoOutputDto {
	private Long id;
	private BigDecimal subtotal;
	private BigDecimal taxaFrete;
	private BigDecimal valorTotal;
	private StatusPedido status;
	private UsuarioOutputDto cliente;
	private RestauranteResumoOutputDto restaurante;
}