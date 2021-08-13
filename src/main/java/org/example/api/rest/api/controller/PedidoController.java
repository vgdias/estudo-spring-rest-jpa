package org.example.api.rest.api.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.example.api.rest.api.model.dto.pedido.PedidoInputDto;
import org.example.api.rest.api.model.dto.pedido.PedidoOutputDto;
import org.example.api.rest.domain.exception.NegocioException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Pedido;
import org.example.api.rest.domain.model.Usuario;
import org.example.api.rest.domain.service.PedidoService;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.example.api.rest.shared.validation.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	@Autowired
	PedidoService pedidoService;

	@GetMapping()
	public List<PedidoOutputDto> listar(HttpServletRequest request) {
		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList());
		List<Pedido> pedidos = pedidoService.listar();
		return GenericMapper.collectionMap(pedidos, PedidoOutputDto.class);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoOutputDto adicionar(@Valid @RequestBody PedidoInputDto pedidoNovoDto) {
		try {
			Pedido pedidoNovo = GenericMapper.map(pedidoNovoDto, Pedido.class);

			pedidoNovo.setCliente(new Usuario());
			pedidoNovo.getCliente().setId(1L);

			pedidoNovo = pedidoService.emitir(pedidoNovo);

			return GenericMapper.map(pedidoNovo, PedidoOutputDto.class);
		} catch (RecursoNaoEncontradoException e) {
			throw new NegocioException(e.getMessage(), e);
		}
	}

	@GetMapping("/{id}")
	public PedidoOutputDto buscarPorId(
			@PathVariable("id") @NotBlank(message = "{notBlank}") String codigo,
			HttpServletRequest request) {

		GenericValidator.validateRequestParams(request.getParameterNames(), Arrays.asList()); 
		Pedido pedido = pedidoService.buscarPedidoPorId(codigo);
		return GenericMapper.map(pedido, PedidoOutputDto.class);
	}
}