package org.example.api.rest.domain.service;

import java.util.List;
import java.util.Objects;

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

	private static final String MSG_ESTADO_NAO_ENCONTRADO = "Estado de id %d nao encontrado";
	private static final String MSG_CIDADE_EM_USO = "Cidade de id %d em uso e nao pode ser removida";
	private static final String MSG_CIDADE_NAO_ENCONTRADA = "Cidade de id %d nao encontrada";

	@Autowired
	private CidadeRepository cidadeRepository;

	@Autowired
	private EstadoRepository estadoRepository;

	public List<Cidade> listar() {
		return cidadeRepository.findAll();
	}

	public Cidade buscar(Long cidadeId) {
		return obtemCidade(cidadeId);
	} 

	@Transactional
	public Cidade adicionar(Cidade cidade) {
		if (Objects.nonNull(cidade.getEstado().getId())) {
			Estado estado = obterEstadoDeCidade(cidade.getEstado().getId());

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
		Long estadoAtualId = cidadeNova.getEstado().getId();
		Estado estadoAtual = obterEstadoDeCidade(estadoAtualId);

		cidadeNova.setEstado(estadoAtual);
		return cidadeRepository.save(cidadeNova);
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

	public Cidade obtemCidade(Long id) {
		return cidadeRepository.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException(
						String.format(MSG_CIDADE_NAO_ENCONTRADA, id)));
	}

	public Estado obterEstadoDeCidade(Long id) {
		return estadoRepository.findById(id)
				.orElseThrow(() -> new DependenciaNaoEncontradaException(
						String.format(MSG_ESTADO_NAO_ENCONTRADO, id)));
	}
}