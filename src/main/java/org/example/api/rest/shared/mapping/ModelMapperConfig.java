package org.example.api.rest.shared.mapping;

import org.example.api.rest.api.model.dto.cidade.CidadeOutputDto;
import org.example.api.rest.api.model.dto.endereco.EnderecoOutputDto;
import org.example.api.rest.api.model.dto.itempedido.ItemPedidoInputDto;
import org.example.api.rest.api.model.dto.restaurante.RestauranteOutputDto;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.model.Endereco;
import org.example.api.rest.domain.model.ItemPedido;
import org.example.api.rest.domain.model.Restaurante;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();

		cidadeToCidadeOutputDtoMappings(mapper);
		itemPedidoInputDtoToItemPedidoMappings(mapper);

		// RestauranteOutputDto possui EnderecoOutputDto.
		// Por isso, o mapeamento de endereco deve ser declarado antes
		// do mapeamento de restaurante
		enderecoToEnderecoOutputDtoMappings(mapper);
		restauranteToRestauranteOutputDtoMappings(mapper);

		return mapper;
	}

	private void itemPedidoInputDtoToItemPedidoMappings(ModelMapper mapper) {
		mapper.createTypeMap(ItemPedidoInputDto.class, ItemPedido.class)
		.addMappings(map -> map.skip(ItemPedido::setId));
	}

	private void restauranteToRestauranteOutputDtoMappings(ModelMapper mapper) {
		TypeMap<Restaurante, RestauranteOutputDto> typeMap = 
				mapper.createTypeMap(Restaurante.class, RestauranteOutputDto.class);

		typeMap.<String>addMapping(
				restaurante -> restaurante.getCozinha().getNome(), 
				(restauranteOutputDto, value) -> restauranteOutputDto.setCozinha(value));		
	}

	private void enderecoToEnderecoOutputDtoMappings(ModelMapper mapper) {
		TypeMap<Endereco, EnderecoOutputDto> typeMap = 
				mapper.createTypeMap(Endereco.class, EnderecoOutputDto.class);

		typeMap.<String>addMapping(
				endereco -> endereco.getCidade().getEstado().getNome(), 
				(enderecoOutputDto, value) -> enderecoOutputDto.setEstado(value));

		typeMap.<String>addMapping(
				endereco -> endereco.getCidade().getNome(), 
				(enderecoOutputDto, value) -> enderecoOutputDto.setCidade(value));
	}

	private void cidadeToCidadeOutputDtoMappings(ModelMapper mapper) {
		TypeMap<Cidade, CidadeOutputDto> typeMap = 
				mapper.createTypeMap(Cidade.class, CidadeOutputDto.class);

		typeMap.<String>addMapping(
				cidade -> cidade.getNome(), 
				(cidadeOutputDto, value) -> cidadeOutputDto.setCidade(value));

		typeMap.<String>addMapping(
				cidade -> cidade.getEstado().getNome(), 
				(cidadeOutputDto, value) -> cidadeOutputDto.setEstado(value));		
	}
}
