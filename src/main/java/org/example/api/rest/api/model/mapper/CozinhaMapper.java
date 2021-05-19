package org.example.api.rest.api.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.example.api.rest.api.model.dto.cozinha.CozinhaInputDto;
import org.example.api.rest.api.model.dto.cozinha.CozinhaOutputDto;
import org.example.api.rest.domain.model.Cozinha;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CozinhaMapper {

	@Autowired
	private ModelMapper mapper;

	public CozinhaOutputDto fromDomainObjectToOutputDto(Cozinha cozinha) {
		return mapper.map(cozinha, CozinhaOutputDto.class);
	}
	public List<CozinhaOutputDto> fromCollectionDomainObjectToCollectionOutputDto(List<Cozinha> cozinhas) {
		return cozinhas.stream()
				.map(cozinha -> fromDomainObjectToOutputDto(cozinha))
				.collect(Collectors.toList());
	}

	public Cozinha fromInputDtoToDomainObject(CozinhaInputDto cozinhaInputDto) {
		return mapper.map(cozinhaInputDto, Cozinha.class);
	}
	public List<Cozinha> fromCollectionInputDtoToCollectionDomainObject(List<CozinhaInputDto> cozinhaInputDtos) {
		return cozinhaInputDtos.stream()
				.map(cozinhaInputDto -> fromInputDtoToDomainObject(cozinhaInputDto))
				.collect(Collectors.toList());
	}
}
