package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.ESTADO_POR_ID_EM_USO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.ESTADO_POR_ID_NAO_ENCONTRADO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.ESTADO_POR_NOME_ENCONTRADO;

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
public class EstadoService {

	@Autowired
	private EstadoRepository estadoRepository;

	public List<Estado> listar() {
		return estadoRepository.findAll();
	}

	public Estado buscarPorId(Long estadoId) {
		return obterEstado(estadoId);
	}

	@Transactional
	public Estado adicionar(Estado estado) {
		verificarSeEstadoExistePorNome(estado);
		return estadoRepository.save(estado);
	}

	@Transactional
	public Estado alterar(Estado estadoNovo) {
		verificarSeEstadoExistePorNome(estadoNovo);
		return estadoRepository.save(estadoNovo);
	}

	@Transactional
	public void remover(Long estadoId) {
		try {
			estadoRepository.deleteById(estadoId);
			estadoRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new RecursoNaoEncontradoException(
					String.format(
							ESTADO_POR_ID_NAO_ENCONTRADO.toString(), 
							estadoId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(
							ESTADO_POR_ID_EM_USO.toString(), 
							estadoId));
		}
	}

	public Estado obterEstado(Long id) {
		return estadoRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								ESTADO_POR_ID_NAO_ENCONTRADO.toString(), 
								id)));
	}

	private void verificarSeEstadoExistePorNome(Estado estado) {
		Optional<Estado> estadoAtual = estadoRepository.findByNome(estado.getNome());
		if (estadoAtual.isPresent()) {
			throw new RecursoEmUsoException(
					String.format(
							ESTADO_POR_NOME_ENCONTRADO.toString(), 
							estado.getNome()));
		}
	}
}