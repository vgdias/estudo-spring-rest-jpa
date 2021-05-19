package org.example.api.rest.api.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.example.api.rest.api.model.dto.restaurante.RestauranteInputDto;
import org.example.api.rest.api.model.dto.restaurante.RestauranteOutputDto;
import org.example.api.rest.domain.model.Restaurante;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestauranteMapper {

	@Autowired
	private ModelMapper mapper;

	public RestauranteOutputDto fromDomainObjectToOutputDto(Restaurante restaurante) {
		return mapper.map(restaurante, RestauranteOutputDto.class);
	}
	public List<RestauranteOutputDto> fromCollectionDomainObjectToCollectionOutputDto(List<Restaurante> restaurantes) {
		return restaurantes.stream()
				.map(restaurante -> fromDomainObjectToOutputDto(restaurante))
				.collect(Collectors.toList());
	}

	public Restaurante fromInputDtoToDomainObject(RestauranteInputDto restauranteInputDto) {
		return mapper.map(restauranteInputDto, Restaurante.class);
	}
	public List<Restaurante> fromCollectionInputDtoToCollectionDomainObject(List<RestauranteInputDto> restauranteInputDtos) {
		return restauranteInputDtos.stream()
				.map(restauranteInputDto -> fromInputDtoToDomainObject(restauranteInputDto))
				.collect(Collectors.toList());
	}
}