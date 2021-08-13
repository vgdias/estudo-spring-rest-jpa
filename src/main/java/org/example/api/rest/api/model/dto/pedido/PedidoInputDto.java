package org.example.api.rest.api.model.dto.pedido;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.example.api.rest.api.model.dto.endereco.EnderecoInputDto;
import org.example.api.rest.api.model.dto.formapagamento.IdFormaPagamentoInputDto;
import org.example.api.rest.api.model.dto.itempedido.ItemPedidoInputDto;
import org.example.api.rest.api.model.dto.restaurante.IdRestauranteInputDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PedidoInputDto {
	@Valid
	@NotNull
	private IdRestauranteInputDto restaurante;

	@Valid
	@NotNull
	private IdFormaPagamentoInputDto formaPagamento;

	@Valid
	@NotNull
	private EnderecoInputDto enderecoEntrega;

	@Valid
	@Size(min = 1)
	@NotNull
	private List<ItemPedidoInputDto> itens;

}       