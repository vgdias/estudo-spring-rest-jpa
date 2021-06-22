package org.example.api.rest.shared.mapping;

import org.example.api.rest.api.model.dto.cidade.CidadeOutputDto;
import org.example.api.rest.api.model.dto.endereco.EnderecoOutputDto;
import org.example.api.rest.domain.model.Cidade;
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

		enderecoToEnderecoOutputDtoMappings(mapper);
		cidadeToCidadeOutputDtoMappings(mapper);

		return mapper;
	}

	private void enderecoToEnderecoOutputDtoMappings(ModelMapper mapper) {
		TypeMap<Endereco, EnderecoOutputDto> enderecoToEnderecoOutputDtpTypeMap = 
				mapper.createTypeMap(Endereco.class, EnderecoOutputDto.class);

		enderecoToEnderecoOutputDtpTypeMap.<String>addMapping(
				endereco -> endereco.getCidade().getEstado().getNome(), 
				(enderecoOutputDto, value) -> enderecoOutputDto.setEstado(value));

		enderecoToEnderecoOutputDtpTypeMap.<String>addMapping(
				endereco -> endereco.getCidade().getNome(), 
				(enderecoOutputDto, value) -> enderecoOutputDto.setCidade(value));
	}

	private void cidadeToCidadeOutputDtoMappings(ModelMapper mapper) {
		TypeMap<Cidade, CidadeOutputDto> cidadeToCidadeOutputDtpTypeMap = 
				mapper.createTypeMap(Cidade.class, CidadeOutputDto.class);

		cidadeToCidadeOutputDtpTypeMap.<String>addMapping(
				cidade -> cidade.getNome(), 
				(cidadeOutputDto, value) -> cidadeOutputDto.setCidade(value));

		cidadeToCidadeOutputDtpTypeMap.<String>addMapping(
				cidade -> cidade.getEstado().getNome(), 
				(cidadeOutputDto, value) -> cidadeOutputDto.setEstado(value));		
	}
}
