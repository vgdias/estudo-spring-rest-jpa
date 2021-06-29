package org.example.api.rest.domain.service;

import java.util.List;
import java.util.Optional;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroEstadoService {

	private static final String MSG_ESTADO_EM_USO = "Estado de id [%d] em uso";
	private static final String MSG_ESTADO_NAO_ENCONTRADO = "Estado de id [%d] nao encontrado";
	private static final String MSG_ESTADO_COM_NOME_EXISTENTE = "Estado [%s] já existe";

	@Autowired
	private EstadoRepository estadoRepository;

	public List<Estado> listar() {
		return estadoRepository.findAll();
	}

	public Estado buscar(Long estadoId) {
		return obterEstado(estadoId);
	}

	@Transactional
	public Estado adicionar(Estado estado) {
		Optional<Estado> estadoAtual = estadoRepository.findByNome(estado.getNome());
		if (estadoAtual.isPresent()) {
			throw new RecursoEmUsoException(
					String.format(MSG_ESTADO_COM_NOME_EXISTENTE, estado.getNome()));
		}
		return estadoRepository.save(estado);
	}

	@Transactional
	public Estado alterar(Estado estadoNovo) {
		return estadoRepository.save(estadoNovo);
	}

		@Transactional
	public void remover(Long estadoId) {
		try {
			estadoRepository.deleteById(estadoId);
			estadoRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new RecursoNaoEncontradoException(
					String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(MSG_ESTADO_EM_USO, estadoId));
		}
	}

	public Estado obterEstado(Long id) {
		return estadoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_ESTADO_NAO_ENCONTRADO, id)));
	}
}
