package org.example.api.rest.shared.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GenericMapper {

	private static ModelMapper mapper;

	@Autowired
	private ModelMapper modelMapper;

	@PostConstruct
	public void init() {
		mapper = modelMapper;
	}

	/**
	 * Converte um objeto de qualquer tipo em um objeto do tipo generico T recebido por parametro.
	 * A operacao substitui as propriedades em comum do objeto de origem no objeto de destino, 
	 * criando um novo objeto do tipo generico T (classeDestino).
	 * @param <T> Tipo generico da classe de destino
	 * @param objetoOrigem Objeto de origem
	 * @param classeDestino Classe de destino
	 * @return Objeto do tipo T com as propriedades do objeto {@code objetoOrigem}
	 */
	public static <T> T map(Object objetoOrigem, Class<T> classeDestino) {
		return mapper.map(objetoOrigem, classeDestino);
	}

	/**
	 * Operacao {@link #map } com uma lista de objetos de origem
	 * @param <T> Tipo generico da classe de destino
	 * @param listaObjetoOrigem Lista de objetos de origem
	 * @param classeDestino Classe de destino
	 * @return Lista de objetos do tipo T com as propriedades dos objetos de {@code listaObjetoOrigem}
	 */
	public static <T> List<T> collectionMap(List<?> listaObjetoOrigem, Class<T> classeDestino) {
		return listaObjetoOrigem.stream()
				.map(objetoOrigem -> GenericMapper.map(objetoOrigem, classeDestino))
				.collect(Collectors.toList());
	}

	/**
	 * Converte um Map com as propriedades de um objeto de qualquer tipo em um objeto 
	 * do tipo generico T recebido por parametro.
	 * Apos criar um objeto de origem do tipo T com as propriedades do Map, a operacao 
	 * substitui as propriedades em comum do objeto de origem no objeto de destino, 
	 * criando um novo objeto do tipo generico T (classeDestino).
	 * O objeto de origem eh criado atraves de um {@code Map<chave, valor>}, onde {@code chave} eh o nome da 
	 * propriedade do objeto e {@code valor} eh o valor desta propriedade. Para cada propriedade do objeto de origem 
	 * em {@code propriedadesObjetoOrigem}, o seu valor eh copiado para o objeto de destino.
	 * @param <T> Tipo generico da classe de destino
	 * @param propriedadesObjetoOrigem Map com as propriedades do objeto de origem
	 * @param objetoDestino Objeto de destino
	 * @param classeDestino Classe de destino
	 */
	public static <T> void map(Map<String, Object> propriedadesObjetoOrigem, Object objetoDestino, Class<T> classeDestino) {
		// remove a propriedade id se houver, para que o objetoDestino nao tenha seu id sobrescrito
		propriedadesObjetoOrigem.remove("id");

		// converte os elementos do Map propriedadesObjetoOrigem em um objeto da classe classeDestino
		T objetoOrigem = new ObjectMapper().convertValue(propriedadesObjetoOrigem, classeDestino);

		propriedadesObjetoOrigem.forEach((nomePropriedade, valor) -> {
			// obtem dinamicamente cada propriedade da classe classeDestino pelo nome
			Field propriedade = ReflectionUtils.findField(classeDestino, nomePropriedade);

			// se a propriedade obtida for privada, eh preciso torna-la acessivel
			propriedade.setAccessible(true);

			// obtem o valor da propriedade do objetoOrigem
			Object valorPropriedade = ReflectionUtils.getField(propriedade, objetoOrigem);

			// atribui dinamicamente o valor da propriedade do objetoOrigem no objetoDestino
			ReflectionUtils.setField(propriedade, objetoDestino, valorPropriedade);
		});
	}
}