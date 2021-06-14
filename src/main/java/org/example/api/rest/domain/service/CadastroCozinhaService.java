package org.example.api.rest.domain.service;

import java.util.List;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Cozinha;
import org.example.api.rest.domain.repository.CozinhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroCozinhaService {

	private static final String MSG_COZINHA_EM_USO = "Cozinha de id %d em uso e nao pode ser removida";
	private static final String MSG_COZINHA_POR_ID_NAO_ENCONTRADA = "Cozinha de id %d nao encontrada";
	private static final String MSG_COZINHA_POR_NOME_NAO_ENCONTRADA = "Cozinha de nome %s nao encontrada";

	@Autowired
	private CozinhaRepository cozinhaRepository;

	public List<Cozinha> listar() {
		return cozinhaRepository.findAll();
	}

	public Cozinha buscar(Long cozinhaId) {
		return obterCozinha(cozinhaId);
	}

	@Transactional
	public Cozinha adicionar(Cozinha cozinha) {
		return cozinhaRepository.save(cozinha);
	}

	@Transactional
	public Cozinha alterar(Cozinha cozinhaNova) {
		return cozinhaRepository.save(cozinhaNova);
	}

		@Transactional
	public void remover(Long cozinhaId) {
		try {
			cozinhaRepository.deleteById(cozinhaId);
			cozinhaRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new RecursoNaoEncontradoException(
					String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, cozinhaId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(MSG_COZINHA_EM_USO, cozinhaId));
		}
	}

	public Cozinha porNome(String nome) {
		return cozinhaRepository.findByNome(nome)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_COZINHA_POR_NOME_NAO_ENCONTRADA, nome)));
	}

	public List<Cozinha> comNomeSemelhante(String nome) {
		return cozinhaRepository.nomeContaining(nome);
	}

	public long count() {
		return cozinhaRepository.count();
	}

	public Cozinha obterCozinha(Long id) {
		return cozinhaRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_COZINHA_POR_ID_NAO_ENCONTRADA, id)));
	}

}