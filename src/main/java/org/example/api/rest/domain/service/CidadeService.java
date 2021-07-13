package org.example.api.rest.domain.service;

import static org.example.api.rest.api.exceptionhandler.ErrorMessage.CIDADE_POR_ID_EM_USO;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.CIDADE_POR_ID_NAO_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.CIDADE_POR_NOME_ENCONTRADA;
import static org.example.api.rest.api.exceptionhandler.ErrorMessage.ESTADO_POR_ID_NAO_ENCONTRADO;

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
public class CidadeService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private EstadoRepository estadoRepository;

	public List<Cidade> listar() {
		return cidadeRepository.findAll();
	}

	public Cidade buscarPorId(Long cidadeId) {
		return buscarCidadePorId(cidadeId);
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
					String.format(
							ESTADO_POR_ID_NAO_ENCONTRADO.toString(), 
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
					String.format(
							ESTADO_POR_ID_NAO_ENCONTRADO.toString(), 
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
					String.format(
							CIDADE_POR_ID_NAO_ENCONTRADA.toString(), 
							cidadeId));
		} catch (DataIntegrityViolationException e) {
			throw new RecursoEmUsoException(
					String.format(
							CIDADE_POR_ID_EM_USO.toString(), 
							cidadeId));
		}
	}

	public Cidade buscarCidadePorId(Long id) {
		return cidadeRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(
								CIDADE_POR_ID_NAO_ENCONTRADA.toString(), 
								id)));
	}

	private Estado obterEstadoDeCidadePorId(Long id) {
		return estadoRepository.findById(id)
				.orElseThrow(() -> new DependenciaNaoEncontradaException(
						String.format(
								ESTADO_POR_ID_NAO_ENCONTRADO.toString(), 
								id)));
	}

	private void verificarSeCidadeEstadoExistePorCidadeNomeEstadoId(Cidade cidade) {
		Optional<Cidade> cidadeAtual = cidadeRepository.findByNomeAndEstadoId(cidade.getNome(), cidade.getEstado().getId());
		if (cidadeAtual.isPresent()) {
			throw new RecursoEmUsoException(
					String.format(
							CIDADE_POR_NOME_ENCONTRADA.toString(), 
							cidade.getNome()));
		}
	}
}