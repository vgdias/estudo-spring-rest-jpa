package org.example.api.rest.domain.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.example.api.rest.domain.exception.DependenciaNaoEncontradaException;
import org.example.api.rest.domain.exception.RecursoEmUsoException;
import org.example.api.rest.domain.exception.RecursoNaoEncontradoException;
import org.example.api.rest.domain.model.Cidade;
import org.example.api.rest.domain.model.Estado;
import org.example.api.rest.domain.repository.CidadeRepository;
import org.example.api.rest.domain.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroCidadeService {

	private static final String MSG_ESTADO_NAO_ENCONTRADO = "Estado de id [%d] nao encontrado";
	private static final String MSG_CIDADE_EM_USO = "Cidade de id [%d] em uso";
	private static final String MSG_CIDADE_NAO_ENCONTRADA = "Cidade de id [%d] nao encontrada";
	private static final String MSG_CIDADE_ENCONTRADA_POR_NOME = "Cidade [%s] já existe";
	private static final String MSG_ESTADO_POR_ID_NAO_ENCONTRADO = "Estado de id [%d] nao encontrado";

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private EstadoRepository estadoRepository;

	public List<Cidade> listar() {
		return cidadeRepository.findAll();
	}

	public Cidade buscar(Long cidadeId) {
		return obterCidadePorId(cidadeId);
	} 

	@Transactional
	public Cidade adicionar(Cidade cidade) {
		verificarSeCidadeEstadoExistePorCidadeNomeEstadoId(cidade);

		Long estadoId = cidade.getEstado().getId();
		if (Objects.nonNull(estadoId)) {
			Estado estado = obterEstadoDeCidadePorId(estadoId);
			cidade.setEstado(estado);
			return cidadeRepository.save(cidade);

		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(MSG_ESTADO_NAO_ENCONTRADO, 
							cidade.getEstado().getId()));
		}
	}

	@Transactional
	public Cidade alterar(Cidade cidadeNova) {
		entityManager.detach(cidadeNova);
		verificarSeCidadeEstadoExistePorCidadeNomeEstadoId(cidadeNova);

		Long estadoAtualId = cidadeNova.getEstado().getId();
		if (Objects.nonNull(estadoAtualId)) {
			Estado estadoAtual = obterEstadoDeCidadePorId(estadoAtualId);
			cidadeNova.setEstado(estadoAtual);
			return cidadeRepository.save(cidadeNova);

		} else {
			throw new DependenciaNaoEncontradaException(
					String.format(MSG_ESTADO_POR_ID_NAO_ENCONTRADO, 
							cidadeNova.getEstado().getId()));
		}
	}

	@Transactional
	public void remover(Long cidadeId) {
		try {
			cidadeRepository.deleteById(cidadeId);
			cidadeRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new RecursoNaoEncontradoException(
					String.format(MSG_CIDADE_NAO_ENCONTRADA, cidadeId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(MSG_CIDADE_EM_USO, cidadeId));
		}
	}

	public Cidade obterCidadePorId(Long id) {
		return cidadeRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_CIDADE_NAO_ENCONTRADA, id)));
	}

	private Estado obterEstadoDeCidadePorId(Long id) {
		return estadoRepository.findById(id)
				.orElseThrow(() -> new DependenciaNaoEncontradaException(
						String.format(MSG_ESTADO_NAO_ENCONTRADO, id)));
	}

	private void verificarSeCidadeEstadoExistePorCidadeNomeEstadoId(Cidade cidade) {
		Optional<Cidade> cidadeAtual = cidadeRepository.findByNomeAndEstadoId(cidade.getNome(), cidade.getEstado().getId());
		if (cidadeAtual.isPresent()) {
			throw new RecursoEmUsoException(
					String.format(MSG_CIDADE_ENCONTRADA_POR_NOME, cidade.getNome()));
		}
	}
}