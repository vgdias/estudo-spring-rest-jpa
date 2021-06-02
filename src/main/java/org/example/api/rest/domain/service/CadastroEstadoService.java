package org.example.api.rest.domain.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.repository.EstadoRepository;
import org.example.api.rest.shared.mapping.GenericMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroEstadoService {

	private static final String MSG_ESTADO_EM_USO = "Estado de id %d em uso e nao pode ser removido";
	private static final String MSG_ESTADO_NAO_ENCONTRADO = "Estado de id %d nao encontrado";

	@Autowired
	private EstadoRepository estadoRepository;

	public List<Estado> listar() {
		return estadoRepository.findAll();
	}

	public Estado buscar(Long estadoId) {
		return estadoRepository.findById(estadoId)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoId)));
	}

	@Transactional
	public Estado adicionar(Estado estado) {
		return estadoRepository.save(estado);
	}

	@Transactional
	public Estado alterar(Map<String, Object> propriedadesEstadoNovo, Long estadoAtualId, HttpServletRequest request) {

		if (propriedadesEstadoNovo.isEmpty()) {
			throw new ValidationException("Nenhuma propriedade foi fornecida");
		}
		if (propriedadesEstadoNovo.containsKey("id")) {
			throw new ValidationException("A propriedade 'estado.id' nao pode ser alterada");
		}
		
		Estado estadoAtual = estadoRepository.findById(estadoAtualId)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoAtualId)));

		GenericMapper.map(propriedadesEstadoNovo, estadoAtual, Estado.class, request);
		
		if (estadoAtual.getNome().trim().isEmpty()) {
			throw new ValidationException("A propriedade 'nome' nao pode ser vazia");
		}
		return estadoRepository.save(estadoAtual);
	}

//	@Transactional
	public void remover(Long estadoId) {
		try {
			estadoRepository.deleteById(estadoId);

		} catch (EmptyResultDataAccessException e) {
			throw new RecursoNaoEncontradoException(
					String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(MSG_ESTADO_EM_USO, estadoId));
		}
	}
}
