package org.example.api.rest.domain.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.api.rest.domain.exception.EntidadeEmUsoException;
import org.example.api.rest.domain.exception.EntidadeNaoEncontradaException;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.repository.CozinhaRepository;
import org.example.api.rest.shared.mapper.GenericMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CadastroCozinhaService {

	private static final String MSG_COZINHA_EM_USO = "Cozinha de codigo %d em uso e nao pode ser removida";
	private static final String MSG_COZINHA_NAO_ENCONTRADA = "Cozinha de codigo %d nao encontrada";

	@Autowired
	private CozinhaRepository cozinhaRepository;

	public List<Cozinha> listar() {
		return cozinhaRepository.findAll();
	}

	public Cozinha buscar(Long cozinhaId) {
		return cozinhaRepository.findById(cozinhaId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_COZINHA_NAO_ENCONTRADA, cozinhaId)));
	}

	@Transactional
	public Cozinha adicionar(Cozinha cozinha) {
		return cozinhaRepository.save(cozinha);
	}

	@Transactional
	public Cozinha alterar(Map<String, Object> propriedadesCozinhaNova, Long cozinhaAtualId) {

		Optional<Cozinha> cozinhaAtualOpt = cozinhaRepository.findById(cozinhaAtualId);
		if (cozinhaAtualOpt.isPresent()) {
			Cozinha cozinhaAtual = cozinhaAtualOpt.get();
			GenericMapper.map(propriedadesCozinhaNova, cozinhaAtual, Cozinha.class);
			return cozinhaRepository.save(cozinhaAtual);
		}
		throw new EntidadeNaoEncontradaException(
				String.format(MSG_COZINHA_NAO_ENCONTRADA, cozinhaAtualId));
	}

	// Utilizando shared.mapper.GenericMapper
	@SuppressWarnings("unused")
	private void merge(Map<String, Object> propriedadesCozinhaNova, Cozinha cozinhaAtual) {
		// remove a propriedade id se houver, para que o cozinhaAtual nao tenha seu id sobrescrito
		propriedadesCozinhaNova.remove("id");

		// converte os elementos do Map propriedadesCozinhaNova em um objeto Cozinha
		Cozinha cozinhaNova = new ObjectMapper().convertValue(propriedadesCozinhaNova, Cozinha.class);

		propriedadesCozinhaNova.forEach((nomePropriedade, valor) -> {
			// obtem dinamicamente uma propriedade da classe Cozinha pelo nome dela
			Field propriedade = ReflectionUtils.findField(Cozinha.class, nomePropriedade);

			// se a propriedade obtida for privada, eh preciso torna-la acessivel
			propriedade.setAccessible(true);

			// obtem o valor da propriedade obtida
			Object valorPropriedade = ReflectionUtils.getField(propriedade, cozinhaNova);

			// atribui dinamicamente o valor da propriedade obtida no objeto cozinhaDestino
			ReflectionUtils.setField(propriedade, cozinhaAtual, valorPropriedade);
		});
	}

	@Transactional
	public void remover(Long cozinhaId) {
		try {
			Optional<Cozinha> cozinhaOpt = cozinhaRepository.findById(cozinhaId);
			if (cozinhaOpt.isPresent()) {
				cozinhaRepository.delete(cozinhaOpt.get());
			} else {
				throw new EntidadeNaoEncontradaException(
						String.format(MSG_COZINHA_NAO_ENCONTRADA, cozinhaId));
			}
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_COZINHA_EM_USO, cozinhaId));
		}
	}

	public Cozinha porNome(String nome) {
		Optional<Cozinha> cozinhaOpt = cozinhaRepository.findByNome(nome);
		if (cozinhaOpt.isPresent()) {
			return cozinhaOpt.get();
		} 
		throw new EntidadeNaoEncontradaException(
				String.format(MSG_COZINHA_NAO_ENCONTRADA, cozinhaOpt.get().getId()));
	}

	public List<Cozinha> comNomeSemelhante(String nome) {
		return cozinhaRepository.nomeContaining(nome);
	}

}