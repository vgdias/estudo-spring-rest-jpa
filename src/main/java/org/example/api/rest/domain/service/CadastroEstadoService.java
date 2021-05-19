package org.example.api.rest.domain.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.api.rest.domain.exception.EntidadeEmUsoException;
import org.example.api.rest.domain.exception.EntidadeNaoEncontradaException;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.repository.EstadoRepository;
import org.example.api.rest.shared.mapper.GenericMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroEstadoService {

	private static final String MSG_ESTADO_EM_USO = "Estado de codigo %d em uso e nao pode ser removido";
	private static final String MSG_ESTADO_NAO_ENCONTRADO = "Estado de codigo %d nao encontrado";

	@Autowired
	private EstadoRepository estadoRepository;

	public List<Estado> listar() {
		return estadoRepository.findAll();
	}

	public Estado buscar(Long estadoId) {
		return estadoRepository.findById(estadoId)
				.orElseThrow(() -> new EntidadeNaoEncontradaException(
						String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoId)));
	}

	@Transactional
	public Estado adicionar(Estado estado) {
		return estadoRepository.save(estado);
	}

	@Transactional
	public Estado alterar(Map<String, Object> propriedadesEstadoNovo, Long estadoAtualId) {

		Optional<Estado> estadoAtualOpt = estadoRepository.findById(estadoAtualId);
		if (estadoAtualOpt.isPresent()) {
			Estado estadoAtual = estadoAtualOpt.get();
			GenericMapper.map(propriedadesEstadoNovo, estadoAtual, Estado.class);
			return estadoRepository.save(estadoAtual);
		}
		throw new EntidadeNaoEncontradaException(
				String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoAtualId));
	}

	@Transactional
	public void remover(Long estadoId) {
		try {
			Optional<Estado> estadoOpt = estadoRepository.findById(estadoId);
			if (estadoOpt.isPresent()) {
				estadoRepository.delete(estadoOpt.get());
			} else {
				throw new EntidadeNaoEncontradaException(
						String.format(MSG_ESTADO_NAO_ENCONTRADO, estadoId));
			}
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format(MSG_ESTADO_EM_USO, estadoId));
		}
	}
}
