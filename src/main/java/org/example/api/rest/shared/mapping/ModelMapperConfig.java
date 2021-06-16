package org.example.api.rest.shared.mapping;

import org.example.api.rest.api.model.dto.endereco.EnderecoOutputDto;
import org.example.api.rest.domain.model.Endereco;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();

		TypeMap<Endereco, EnderecoOutputDto> enderecoToEnderecoOutputDtpTypeMap = 
				mapper.createTypeMap(Endereco.class, EnderecoOutputDto.class);
		enderecoToEnderecoOutputDtpTypeMap.<String>addMapping(
				endereco -> endereco.getCidade().getEstado().getNome(), 
				(enderecoOutputDto, value) -> enderecoOutputDto.getCidade().setEstado(value));
		
		return mapper;
	}
}
