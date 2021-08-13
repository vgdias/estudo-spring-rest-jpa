package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.CIDADE_POR_ID_NAO_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.COZINHA_POR_ID_EM_USO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.COZINHA_POR_ID_NAO_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.COZINHA_POR_NOME_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.COZINHA_POR_NOME_NAO_ENCONTRADA;

import java.util.List;
import java.util.Optional;

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
public class CozinhaService {

	@Autowired
	private CozinhaRepository cozinhaRepository;

	public List<Cozinha> listar() {
		return cozinhaRepository.findAll();
	}

	public Cozinha buscarPorId(Long cozinhaId) {
		return buscarCozinhaPorId(cozinhaId);
	}

	@Transactional
	public Cozinha adicionar(Cozinha cozinha) {
		verificarSeCozinhaExistePorNome(cozinha);
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
					String.format(
							CIDADE_POR_ID_NAO_ENCONTRADA.toString(), 
							cozinhaId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(
							COZINHA_POR_ID_EM_USO.toString(), 
							cozinhaId));
		}
	}

	public Cozinha porNome(String nome) {
		return cozinhaRepository.findByNome(nome)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								COZINHA_POR_NOME_NAO_ENCONTRADA.toString(), 
								nome)));
	}

	public List<Cozinha> comNomeSemelhante(String nome) {
		return cozinhaRepository.nomeContaining(nome);
	}

	public long count() {
		return cozinhaRepository.count();
	}

	public Cozinha buscarCozinhaPorId(Long id) {
		return cozinhaRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								COZINHA_POR_ID_NAO_ENCONTRADA.toString(), 
								id)));
	}

	private void verificarSeCozinhaExistePorNome(Cozinha cozinha) {
		Optional<Cozinha> cozinhaAtual = cozinhaRepository.findByNome(cozinha.getNome());
		if (cozinhaAtual.isPresent()) {
			throw new RecursoEmUsoException(
					String.format(
							COZINHA_POR_NOME_ENCONTRADA.toString(), 
							cozinha.getNome()));
		}
	}
}